(ns thumbnailz.core-test
  (:require [clojure.test :refer :all]
            [thumbnailz.core :refer :all]
            [clojure.java.io :refer [file delete-file]]
            )
  (:import
    [javax.imageio IIOException]
    [java.awt.image BufferedImage]
    )
  )

(def test-resources "test/resources/")
(def test-image (str test-resources "test.png"))
(def test-convert-image (str test-resources "convert.jpg"))
(def test-portrait (str test-resources "portrait.jpg"))
(def test-landscape (str test-resources "landscape.jpg"))

(defn clean-files
  [f]
  (f)
  (let [directory (file test-resources)
        files (file-seq directory)
        ]
    (doseq [f files]
      (if (and (not (= (str f "/") test-resources))
               (not (= (str f) test-image))
               (not (= (str f) test-convert-image))
               (not (= (str f) test-portrait))
               (not (= (str f) test-landscape))
               )
        (delete-file f))
      )
    ))

(use-fixtures :each clean-files)

(deftest thumbnailz.core
  (testing "png-path?"
    (is (= false (png-path? "test_suffix.jpg")))
    (is (= false (png-path? "test_suffix.gif")))
    (is (= false (png-path? "folder/test_suffix.jpg")))
    (is (= false (png-path? "/folder/test_suffix.jpg")))
    (is (= true (png-path? "/folder/test_suffix.png")))
    (is (= true (png-path? "folder/test_suffix.png")))
    (is (= true (png-path? "image.png")))
    )

  (testing "change-path-extension"
    (is (= "test.ext" (change-path-extension "test.extold" "ext")))
    (is (= "dir/test.png" (change-path-extension "dir/test.ext" "png")))
    (is (= "/test.jpg" (change-path-extension "/test.ext" "jpg")))
    )

  (testing "apply-suffix-to-filename"
    (is (= "test_suffix.ext" (apply-suffix-to-filename "test.ext" "_suffix")))
    (is (= "a/test_suffix.ext" (apply-suffix-to-filename "a/test.ext" "_suffix")))
    (is (= "/test_suffix.ext" (apply-suffix-to-filename "/test.ext" "_suffix")))
    )

  (testing "load-image-from-path"
    (is (thrown? IIOException (load-image-from-path "invalid")))
    (is (= BufferedImage (type (load-image-from-path test-image))))
    )

  (testing "get-image-object-width"
    (let [image (load-image-from-path test-image)]
      (is (= 900 (get-image-object-width image)))
    ))

  (testing "get-image-object-height"
    (let [image (load-image-from-path test-image)]
      (is (= 900 (get-image-object-height image)))
    ))

  (testing "get-image-info"
    (is (thrown? IIOException (get-image-info "invalid")))
    (is (= {:width 900 :height 900} (get-image-info test-image)))
    (is (= {:width 800 :height 800} (get-image-info test-convert-image)))
    )

  (testing "convert-to-png"
    (is (thrown? IIOException (convert-to-png "invalid")))
    (is (=
          (change-path-extension test-convert-image "png")
          (convert-to-png test-convert-image)))
    )

  (testing "save-image-to-path"
    (let [image (load-image-from-path test-image)]
         (is (= (str test-resources "saved-image.png") (save-image-to-path image (str test-resources "saved-image.png"))))
    ))

  (testing "resize-image"
    (is (thrown? IIOException (resize-image (load-image-from-path "invalid") 200 200)))
    (is (thrown? IIOException (resize-image (load-image-from-path "invalid") 200)))
    (let [resized-image (resize-image (load-image-from-path test-convert-image) 100 100)
          resized-portrait (resize-image (load-image-from-path test-portrait) 100)
          resized-landscape (resize-image (load-image-from-path test-landscape) 100)]

         (is (= 100 (get-image-object-width resized-image)))
         (is (= 100 (get-image-object-height resized-image)))

         (is (= 50 (get-image-object-width resized-portrait)))
         (is (= 100 (get-image-object-height resized-portrait)))

         (is (= 100 (get-image-object-width resized-landscape)))
         (is (= 66 (get-image-object-height resized-landscape)))
    )
  )

  (testing "resize-image-and-save"
    (is (thrown? IIOException (resize-image-and-save "invalid" test-resources 200 200)))
    (is (thrown? IIOException (resize-image-and-save "invalid" test-resources 200)))
    (let [output-test-image-path (apply-suffix-to-filename test-image "_resized")
          output-test-portrait-path (apply-suffix-to-filename test-portrait "_resized")]
      (is (= output-test-image-path (resize-image-and-save test-image output-test-image-path 200 200)))
      (is (= output-test-portrait-path (resize-image-and-save test-portrait output-test-portrait-path 200)))
    )

  )

  (testing "crop-square"
    (is (thrown? IIOException
                 (crop-square "invalid" 1 1)))
    (is (= (str test-resources "test_400x400.png")
           (crop-square test-image 400 "_400x400")))
    (is (= BufferedImage
           (type (load-image-from-path (str test-resources "test_400x400.png")))))
    (is (= 400
           (get-image-object-width (load-image-from-path (str test-resources "test_400x400.png")))))
    (is (= 400
           (get-image-object-height (load-image-from-path (str test-resources "test_400x400.png")))))

    (is (= (str test-resources "convert_200x200.png")
           (crop-square test-convert-image 200 "_200x200")))

    (is (= (str test-resources "landscape_land_150x150.png")
           (crop-square test-landscape 150 "_land_150x150")))
    (is (= 150
           (get-image-object-width (load-image-from-path (str test-resources "landscape_land_150x150.png")))))
    (is (= 150
           (get-image-object-height (load-image-from-path (str test-resources "landscape_land_150x150.png")))))

    (is (= (str test-resources "portrait_port_150x150.png")
           (crop-square test-portrait 150 "_port_150x150")))
    (is (= 150
           (get-image-object-width (load-image-from-path (str test-resources "portrait_port_150x150.png")))))
    (is (= 150
           (get-image-object-height (load-image-from-path (str test-resources "portrait_port_150x150.png")))))
    )

  (testing "crop-circle"
    (is (thrown? IIOException
                 (crop-circle "invalid" 100 "_100")))
    (is (= (str test-resources "test_circle150.png")
           (crop-circle test-image 150 "_circle150")))
    (is (= BufferedImage
           (type (load-image-from-path (str test-resources "test_circle150.png")))))
    (is (= 150
           (get-image-object-width (load-image-from-path (str test-resources "test_circle150.png")))))
    (is (= 150
           (get-image-object-height (load-image-from-path (str test-resources "test_circle150.png")))))

    (is (= (str test-resources "convert_circle.png")
           (crop-circle test-convert-image 150 "_circle")))
    )

  )
