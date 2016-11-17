# thumbnailz

Thumbnail generator library for Clojure.

Based on [mikera/imagez](https://github.com/mikera/imagez) this library is useful to convert, resize and crop images.

## Documentation

#### REPL

```clojure
(use 'thumbnailz.core)
```

#### png-path?

Usage: `(png-path? image-path)`

Returns true if image-path is a string that ends with .png extension

Example:
```clojure
(png-path? "some/dir/file.png")
=> true

(png-path? "some/dir/file.jpg")
=> false
```

#### change-path-extension

Usage: `(change-path-extension file-path new-ext)`

Returns file-path with new-ext as file extension

Example:
```clojure
(change-path-extension "./image.jpg" "png")
=> "./image.png"
```

#### apply-suffix-to-filename

Usage: `(apply-suffix-to-filename file-path suffix)`

Returns file-path with suffix appended to the file name

Example:
```clojure
(apply-suffix-to-filename "./image.png" "_WxH")
=> "./image_WxH.png"

(apply-suffix-to-filename "./image.png" "-circle")
=> "./image-circle.png"
```

#### load-image-from-path

Usage: `(load-image-from-path image-path)`

Returns a BufferedImage loaded from image-path

Example:
```clojure
(load-image-from-path "./image.png")
=> #object[java.awt.image.BufferedImage ...]
```

#### get-image-object-width

Usage: `(get-image-object-width image)`

Returns the width of the given image object

Example:
```clojure
(get-image-object-width (load-image-from-path "./image.png"))
=> 300
```

#### get-image-object-height

Usage: `(get-image-object-height image)`

Returns the height of the given image object

Example:
```clojure
(get-image-object-height (load-image-from-path "./image.png"))
=> 300
```

#### get-image-info

Usage: `(get-image-info image-path)`

Returns information about the image located at image-path

Example:
```clojure
(get-image-info "./image.png")
=> {:width 300, :height 300}
```

#### convert-to-png

Usage: `(convert-to-png image-path)`

Returns the path of the new png created from image-path file

Saves the new png at the same level of image-path

Example:
```clojure
(convert-to-png "some/dir/image.jpg")
=> "some/dir/image.png"
```

#### save-image-to-path

Usage: `(save-image-to-path image dest-path)`

Saves the image to the dest-path and returns the dest-path

Example:
```clojure
(save-image-to-path (load-image-from-path "./image.png") "destination/output.png")
=> "destination/output.png"
```

#### resize-image

Usage: `(resize-image image-path width height)`
Usage: `(resize-image image-path dimension)`

Returns the BufferedImage resized by width and height.
If only one dimension is specified, the biggest between widht and height is resized, maintaing the ratio of the other one.

Example:
```clojure
(resize-image "./image.png" 300 300)
=> #object[java.awt.image.BufferedImage ...]

(resize-image "./image.png" 300)
=> #object[java.awt.image.BufferedImage ...]
```

#### resize-image-and-save

Usage: `(resize-image-and-save image-path dest-path width height)`
Usage: `(resize-image-and-save image-path dest-path dimension)`

Returns the dest-path of the resized image located in src-path.

Saves the image to the dest-path.

Example:
```clojure
(resize-image-and-save "./image.png" "./dest/output.png" 300 300)
=> "./dest/output.png"

;no conversion applied
(resize-image-and-save "./image.jpg" "./dest/output.jpg" 300)
=> "./dest/output.jpg"
```

#### crop-square

Usage: `(crop-square image-path dimension suffix)`

Returns the path of a new square image resized from image-path with width and height euqal to dimension.

Saves the new png at the same level of image-path applying suffix to file name.

Example:
```clojure
(crop-square "some/dir/image.png" 200 "_200x200")
=> "some/dir/image_200x200.png"

;automatic conversion in png
(crop-square "some/dir/image.jpg" 200 "_200x200")
=> "some/dir/image_200x200.png"
```   

#### crop-circle

Usage: `(crop-circle image-path dimension suffix)`

Returns the path of a new circle image cropped from image-path with width and height equal to dimension.

Saves the new png at the same level of image-path applying suffix to file name.

Example:
```clojure
(crop-circle "some/dir/image.png" 150 "_circle")
=> "some/dir/image_circle.png"

;automatic conversion in png
(crop-circle "some/dir/image.jpg" 150 "_circle")
=> "some/dir/image_circle.png"
```

## License

MIT.
