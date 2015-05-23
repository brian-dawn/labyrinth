(ns labyrinth.core)

;; Provide a sandbox and file-system functionality.

(defn join
  "Join Path instances together."
  [& paths]
  (let [resolve (fn [a b] (.resolve a b))]
    (reduce resolve paths)))

(defn path
  "Create an instance of Path from a string.
     If multiple strings are passed in attempt to create a joined path."
  [& strings]
  (let [creator (fn [s]  (.toPath (java.io.File. s)))]
    (reduce join (map creator strings))))

(defn pwd
  "Return the absolute path to the current working directory
   as a Path instance"
  []
  (path (.getAbsolutePath (new java.io.File "."))))

(def empty-attribute-array (make-array java.nio.file.attribute.FileAttribute 0))
(def root (.getRoot (pwd)))
(def seperator (java.io.File/separator))

(defn- -create-tmp-dir
  "Private wrapper, ignores any file attributes."
  [prefix]
  (java.nio.file.Files/createTempDirectory
   prefix
   empty-attribute-array))

(defn create-tmp-dir
  "Create a temp directory and return the Path instance that references it.
   Takes an optional String that acts as a prefix for the temp directory."
  ([prefix] (-create-tmp-dir prefix))
  ([]       (-create-tmp-dir "")))

(defn md5 [path]
  (let [bytes (java.nio.file.Files/readAllBytes path)
        hash (.digest (java.security.MessageDigest/getInstance "MD5") bytes)]
    (javax.xml.bind.DatatypeConverter/printHexBinary hash)))

(defn remove-root
  "Remove a root component from a Path instance returning a new Path."
  [path]
  (.subpath path 0 (.getNameCount path)))

(defn sandbox
  "Return a new Path instance with the sandbox-path prepended to path."
  [sandbox-path path]
  (join sandbox-path (remove-root path)))

(defn end
  "Get the last element of a Path instance."
  [path]
  (.getFileName path))

(defn trail
  "Get the parents of a Path instance."
  [path]
  (.getParent path))

(defn- sandboxify
  "Make a sandboxed version of an IO function."
  [f]
  (fn [sandbox-path path]  (f (sandbox sandbox-path path))))

;; IO functions.
;; The private versions are so we can make with-sandbox work.
;; Probbly the wrong way to do it.

(defn- -mkdirs
  [path]
  (java.nio.file.Files/createDirectories path empty-attribute-array))

(defn mkdirs
  "Create a directory while creating any parent directories that don't already exist"
  [path]
  (-mkdirs path))

(defn- -touch
  [path]
  (-mkdirs (trail path))
  (java.nio.file.Files/createFile path empty-attribute-array))

(defn touch
  "Create an empty file, creating any parent directories if they don't exist."
  [path]  (-touch path))

(defmacro with-sandbox [sandbox-path expr]
  `(with-redefs-fn {(var touch)  (partial (sandboxify -touch)  ~sandbox-path)
                    (var mkdirs) (partial (sandboxify -mkdirs) ~sandbox-path)}
     (fn [] ~expr)))

; (with-sandbox (create-tmp-dir "prefix")
;  (list (touch (path "foo"))
;        (touch (path "/root-dir/file.txt"))
;        (mkdirs (path "my-folder/somedir"))))

