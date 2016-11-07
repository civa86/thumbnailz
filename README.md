# thumbnailz

Thumbnail generator library for Clojure.

Based on [mikera/imagez](https://github.com/mikera/imagez) this library is useful to convert, resize and crop images.

## Documentation

#### REPL

```clojure
(use 'mikera.image.core)
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
   
#### create-thumbnail

Usage: `(create-thumbnail image-path width height)`

Returns the path of a new image resized from image-path with width and height

Saves the new png at the same level of image-path.
   
Example: 
```clojure
(create-thumbnail "some/dir/image.png" 200 200)
=> "some/dir/image_200x200.png"

;automatic conversion in png
(create-thumbnail "some/dir/image.jpg" 200 200)
=> "some/dir/image_200x200.png"
```   
   
#### crop-circle

Usage: `(crop-circle image-path)`

Returns the path of a new circle image cropped from image-path

Saves the new png at the same level of image-path

Example: 
```clojure
(crop-circle "some/dir/image.png")
=> "some/dir/image_circle.png"

;automatic conversion in png
(crop-circle "some/dir/image.jpg")
=> "some/dir/image_circle.png"
```

## License

MIT.
