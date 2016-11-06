(ns thumbnailz.core-test
  (:require [clojure.test :refer :all]
            [thumbnailz.core :refer :all]
            [clojure.java.io :refer [file delete-file]]
            [mikera.image.core :as img] ;TODO wrap all need methods
            )
  (:import
    [javax.imageio IIOException]
    [java.awt.image BufferedImage]
    )
  )

(def test-resources "test/resources/")
(def test-image (str test-resources "test.png"))
(def test-convert-image (str test-resources "convert.jpg"))

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
               )
        (delete-file f)
        )
      )
    ))

(use-fixtures :each clean-files)

(deftest thumbnailz.image.utilities
  (testing "png-file?"
    (is (= false (png-file? "test_suffix.jpg")))
    (is (= false (png-file? "test_suffix.gif")))
    (is (= false (png-file? "folder/test_suffix.jpg")))
    (is (= false (png-file? "/folder/test_suffix.jpg")))
    (is (= true (png-file? "/folder/test_suffix.png")))
    (is (= true (png-file? "folder/test_suffix.png")))
    (is (= true (png-file? "image.png")))
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

  (testing "create-thumbnail"
    (is (thrown? IIOException
                 (create-thumbnail "invalid" 1 1)))
    (is (= (str test-resources "test_400x400.png")
           (create-thumbnail test-image 400 400)))
    (is (= BufferedImage
           (type (load-image-from-path (str test-resources "test_400x400.png")))))
    (is (= 400
           (img/width (load-image-from-path (str test-resources "test_400x400.png")))))
    (is (= 400
           (img/height (load-image-from-path (str test-resources "test_400x400.png")))))

    (is (= (str test-resources "convert_200x200.png")
           (create-thumbnail test-convert-image 200 200)))
    )

  (testing "crop-circle-png"
    (is (thrown? IIOException
                 (crop-circle-png "invalid")))
    (is (= (str test-resources "test_400x400_circle.png")
           (crop-circle-png (str test-resources "test_400x400.png"))))
    (is (= BufferedImage
           (type (load-image-from-path (str test-resources "test_400x400_circle.png")))))
    (is (= 400
           (img/width (load-image-from-path (str test-resources "test_400x400_circle.png")))))
    (is (= 400
           (img/height (load-image-from-path (str test-resources "test_400x400_circle.png")))))

    (is (= (str test-resources "convert_circle.png")
           (crop-circle-png test-convert-image)))
    )

  )



