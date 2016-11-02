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
;TODO description
(defn is-png
  [img-path]
  (let [path (str/split img-path #"\/")
        file-parts (str/split (last path) #"\.")]
    (= "png" (str/lower-case (last file-parts)))
    ))

;TODO test description
(defn apply-png-ext-to-path
  [src-path]
  (let [path (str/split src-path #"\/")
        file-parts (str/split (last path) #"\.")
        file-name (str (first file-parts) ".png")]
    (if (> (count (drop-last path)) 0)
      (str (str/join "/" (drop-last path)) "/" file-name)
      file-name)))

(defn get-suffixed-path
  "apply suffix to the filename of src-path and returns the whole new path"
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

(defn get-thumb-path
  "returns the thumbanil path with dimensions"
  [src-path width height]
  (get-suffixed-path src-path (str "_" width "x" height)))

(defn get-circle-thumb-path
  "returns the circle thumbanil path"
  [src-path]
  (get-suffixed-path src-path "_circle"))

(defn load-image-from-path
  [image-path]
  (img/load-image (file image-path))
  )

;TODO test description
(defn convert-to-png
  [img-path]
  (if (not (is-png img-path))
    (let [image (load-image-from-path img-path)
          png-path (apply-png-ext-to-path img-path)]
      (ImageIO/write image "png" (file png-path))
      png-path)
    img-path))

(defn do-image-resize
  "Resize an image and save on disk"
  [src-path width height]
  (let [src-image (load-image-from-path src-path)]
    (img/save
      (img/resize src-image width height)
      (get-thumb-path src-path width height))))

(defn do-image-crop-circle
  "Crop a circled image and save on disk"
  [src-path]
  (let [src-image (load-image-from-path src-path)
        output-image (img/new-image
                       (img/width src-image)
                       (img/height src-image)
                       )
        ^Graphics2D g2 (.createGraphics output-image)
        ]
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
      (get-circle-thumb-path src-path))))

;TODO test description
(defn thumblr
  [src-path width height]
  (let [png (convert-to-png src-path)
        thumb (do-image-resize png width height)
        circle (do-image-crop-circle thumb)
        ]
    {
     :raw png
     :thumb thumb
     :circle circle
     }))
