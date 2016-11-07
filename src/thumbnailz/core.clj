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

(defn png-path?
  "Returns true if image-path is a string that ends with .png extension."
  [image-path]
  (let [path (str/split image-path #"\/")
        file-parts (str/split (last path) #"\.")]
    (= "png" (str/lower-case (last file-parts)))
    ))

(defn change-path-extension
  "Returns file-path with new-ext as file extension."
  [file-path new-ext]
  (let [path (str/split file-path #"\/")
        file-parts (str/split (last path) #"\.")
        file-name (str (first file-parts) "." new-ext)]
    (if (> (count (drop-last path)) 0)
      (str (str/join "/" (drop-last path)) "/" file-name)
      file-name)))

(defn apply-suffix-to-filename
  "Returns file-path with suffix appended to the file name."
  [file-path suffix]
  (let [path (str/split file-path #"\/")
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
  "Returns a BufferedImage loaded from image-path."
  [image-path]
  (img/load-image (file image-path)))

(defn get-image-info
  "Returns information about the image located at image-path."
  [image-path]
  (let [image (load-image-from-path image-path)]
    {
     :width (img/width image)
     :height (img/height image)
     }
    ))

(defn convert-to-png
  "Returns the path of the new png created from image-path file.
   Saves the new png at the same level of image-path."
  [image-path]
  (let [image (load-image-from-path image-path)
        png-path (change-path-extension image-path "png")]
    (ImageIO/write image "png" (file png-path))
    png-path)
  )

(defn create-thumbnail
  "Returns the path of a new image resized from image-path with width and height.
   Saves the new png at the same level of image-path."

  [image-path width height]
  (cond
    (png-path? image-path) (let [src-image (load-image-from-path image-path)]
                           (img/save
                             (img/resize src-image width height)
                             (apply-suffix-to-filename image-path (str "_" width "x" height))))
    :else (create-thumbnail (convert-to-png image-path) width height)))

(defn crop-circle
  "Returns the path of a new circle image cropped from image-path.
   Saves the new png at the same level of image-path."
  [image-path]
  (cond
    (png-path? image-path) (let [src-image (load-image-from-path image-path)
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
                             (apply-suffix-to-filename image-path (str "_circle"))))

    :else (crop-circle (convert-to-png image-path))))
