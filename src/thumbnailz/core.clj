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
    [org.imgscalr Scalr]
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

  (defn get-image-object-width
    "Returns the width of the given image object"
    [image]
    (img/width image))

  (defn get-image-object-height
    "Returns the height of the given image object"
    [image]
    (img/height image))

  (defn get-image-info
    "Returns information about the image located at image-path."
    [image-path]
    (let [image (load-image-from-path image-path)]
      { :width (get-image-object-width image) :height (get-image-object-height image) }))

  (defn convert-to-png
    "Returns the path of the new png created from image-path file.
     Saves the new png at the same level of image-path."
    [image-path]
    (let [image (load-image-from-path image-path)
          png-path (change-path-extension image-path "png")]
      (ImageIO/write image "png" (file png-path))
      png-path))

  (defn save-image-to-path
    "Saves the image to the dest-path and returns the dest-path"
    [image dest-path]
    (img/save image dest-path))

  (defn resize-image
    "Returns the BufferedImage resized by width and height.
     If only one dimension is specified, the biggest between widht and height is resized, maintaing the ratio of the other one."

    ([src-image width height]
      (img/resize src-image width height))

    ([src-image dimension]
      (let [src-image-width (get-image-object-width src-image)
            src-image-height (get-image-object-height src-image)]
           (cond
               (> src-image-width src-image-height)
                    (let [calculated-dimension (/ (* (long dimension) src-image-height) src-image-width)]
                         (resize-image src-image dimension calculated-dimension))

               :else (let [calculated-dimension (/ (* (long dimension) src-image-width) src-image-height)]
                          (resize-image src-image calculated-dimension dimension))
           ))
    )
  )

  (defn resize-image-and-save
    "Returns the dest-path of the resized image located in src-path.
     Saves the image to the dest-path."

    ([src-path dest-path width height]
        (save-image-to-path (resize-image (load-image-from-path src-path) width height) dest-path))

    ([src-path dest-path dimension]
        (save-image-to-path (resize-image (load-image-from-path src-path) dimension) dest-path))
  )

  (defn crop-square
    "Returns the path of a new square image resized from image-path with width and height euqal to dimension.
     If the image is not squared, it will be cropped from center by the min dimension.
     Saves the new png at the same level of image-path applying suffix to file name."

    [image-path dimension suffix]
    (cond
      (png-path? image-path) (let [img-dimensions (get-image-info image-path)]
                                (cond
                                    (= (:width img-dimensions) (:height img-dimensions))
                                        (resize-image-and-save image-path
                                                               (apply-suffix-to-filename image-path suffix)
                                                               dimension
                                                               dimension)
                                    :else (let [min-dim (min (:width img-dimensions) (:height img-dimensions))
                                                cropped (Scalr/crop
                                                    (load-image-from-path image-path)
                                                    (- (/ (:width img-dimensions) 2) (/ min-dim 2))
                                                    (- (/ (:height img-dimensions) 2) (/ min-dim 2))
                                                    min-dim
                                                    min-dim
                                                    nil)]
                                            (save-image-to-path
                                                (resize-image cropped dimension dimension)
                                                (apply-suffix-to-filename image-path suffix)))))

      :else (crop-square (convert-to-png image-path) dimension suffix)))

  (defn crop-circle
    "Returns the path of a new circle image cropped from image-path with width and height equal to dimension.
     Saves the new png at the same level of image-path applying suffix to file name."
    [image-path dimension suffix]
    (cond
      (png-path? image-path) (let [src-image (img/resize (load-image-from-path image-path) dimension dimension)
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
                               (apply-suffix-to-filename image-path suffix)))

      :else (crop-circle (convert-to-png image-path) dimension suffix)))
