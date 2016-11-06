(ns thumbnailz.core
  (:require
    [mikera.image.core :as img]
    [clojure.java.io :refer [file]]
    [clojure.string :as str]
    )
  (:import
    [java.awt Graphics2D AlphaComposite RenderingHints Color]
    [java.awt.geom RoundRectangle2D$Float]
    [javax.imageio ImageIO]
    )
  )

(defn png-file?
  "Check if the input string path has the png extension"
  [img-path]
  (let [path (str/split img-path #"\/")
        file-parts (str/split (last path) #"\.")]
    (= "png" (str/lower-case (last file-parts)))
    ))

(defn change-path-extension
  "Returns the input string path changing file extension"
  [src-path new-ext]
  (let [path (str/split src-path #"\/")
        file-parts (str/split (last path) #"\.")
        file-name (str (first file-parts) "." new-ext)]
    (if (> (count (drop-last path)) 0)
      (str (str/join "/" (drop-last path)) "/" file-name)
      file-name)))

(defn apply-suffix-to-filename
  "apply suffix to the filename of input string path and returns the whole new path"
  [src-path suffix]
  (let [path (str/split src-path #"\/")
        file-parts (str/split (last path) #"\.")
        suffixed-file-name (str
                             (first file-parts)
                             suffix
                             "." (last file-parts))
        ]
    (if (> (count (drop-last path)) 0)
      (str (str/join "/" (drop-last path)) "/" suffixed-file-name)
      suffixed-file-name)))

(defn load-image-from-path
  [image-path]
  (img/load-image (file image-path)))

(defn get-image-info
  "Returns image informations"
  [src-path]
  (let [image (load-image-from-path src-path)]
    {
     :width (img/width image)
     :height (img/height image)
     }
    ))

(defn convert-to-png
  "Load image from input string path and save into png"
  [img-path]
  (let [image (load-image-from-path img-path)
        png-path (change-path-extension img-path "png")]
    (ImageIO/write image "png" (file png-path))
    png-path)
  )

(defn create-thumbnail
  "Creates a thumbnail of giver width height"
  [src-path width height]
  (cond
    (png-file? src-path) (let [src-image (load-image-from-path src-path)]
                           (img/save
                             (img/resize src-image width height)
                             (apply-suffix-to-filename src-path (str "_" width "x" height))))
    :else (create-thumbnail (convert-to-png src-path) width height)))

(defn crop-circle-png
  "Crop a circled image and save on disk"
  [src-path]
  (cond
    (png-file? src-path) (let [src-image (load-image-from-path src-path)
                               output-image (img/new-image
                                              (img/width src-image)
                                              (img/height src-image)
                                              )
                               ^Graphics2D g2 (.createGraphics output-image)]
                           (.setComposite g2 AlphaComposite/Src)
                           (.setRenderingHint g2
                                              RenderingHints/KEY_ANTIALIASING
                                              RenderingHints/VALUE_ANTIALIAS_ON)

                           (.setColor g2 Color/WHITE)
                           (.fill g2 (RoundRectangle2D$Float.
                                       0 0
                                       (img/width src-image) (img/height src-image)
                                       (img/width src-image) (img/width src-image)))
                           (.setComposite g2 AlphaComposite/SrcAtop)
                           (.drawImage g2 src-image 0 0 nil)
                           (.dispose g2)

                           (img/save
                             output-image
                             (apply-suffix-to-filename src-path (str "_circle"))))

    :else (crop-circle-png (convert-to-png src-path))))
