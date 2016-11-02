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

(defn clean-files
  [f]
  (f)
  (let [directory (file test-resources)
        files (file-seq directory)
        ]
    (doseq [f files]
      (if (and (not (= (str f "/") test-resources))
               (not (= (str f) test-image)))
        (delete-file f)
        )
      )
    ))

(use-fixtures :each clean-files)

(deftest thumbler
  (testing "test if path is a png file"
    (is (= false (is-png "test_suffix.jpg")))
    (is (= false (is-png "test_suffix.gif")))
    (is (= false (is-png "folder/test_suffix.jpg")))
    (is (= false (is-png "/folder/test_suffix.jpg")))
    (is (= true (is-png "/folder/test_suffix.png")))
    (is (= true (is-png "folder/test_suffix.png")))
    (is (= true (is-png "image.png")))
    )

  (testing "apply a suffix to a file path"
    (is (= "test_suffix.ext" (get-suffixed-path "test.ext" "_suffix")))
    (is (= "a/test_suffix.ext" (get-suffixed-path "a/test.ext" "_suffix")))
    (is (= "/test_suffix.ext" (get-suffixed-path "/test.ext" "_suffix")))
    )

  (testing "generate thumbnail path with dimensions"
    (is (= "test_10x10.ext" (get-thumb-path "test.ext" 10 10)))
    (is (= "test_10x30.ext" (get-thumb-path "test.ext" 10 30)))
    )

  (testing "generate circle thumbnail path"
    (is (= "test_circle.ext" (get-circle-thumb-path "test.ext")))
    )

  (testing "load buffered image from path"
    (is (thrown? IIOException
                 (load-image-from-path "invalid")))
    (is (= BufferedImage
           (type (load-image-from-path "test/resources/test.png"))))
    )

  (testing "resize an image giving width and height"
    (is (thrown? IIOException
                 (do-image-resize "invalid" 1 1)))
    (is (= (str test-resources "test_400x400.png")
           (do-image-resize test-image 400 400)))
    (is (= BufferedImage
           (type (load-image-from-path (str test-resources "test_400x400.png")))))
    (is (= 400
           (img/width (load-image-from-path (str test-resources "test_400x400.png")))))
    (is (= 400
           (img/height (load-image-from-path (str test-resources "test_400x400.png")))))
    )

  (testing "crop a circle image"
    (is (thrown? IIOException
                 (do-image-crop-circle "invalid")))
    (is (= (str test-resources "test_400x400_circle.png")
           (do-image-crop-circle (str test-resources "test_400x400.png"))))
    (is (= BufferedImage
           (type (load-image-from-path (str test-resources "test_400x400_circle.png")))))
    (is (= 400
           (img/width (load-image-from-path (str test-resources "test_400x400_circle.png")))))
    (is (= 400
           (img/height (load-image-from-path (str test-resources "test_400x400_circle.png")))))
    )
  )



