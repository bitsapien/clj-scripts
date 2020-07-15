(defn file-read
  [path]
  (slurp path))

(defn extract-command
  [history-line]
  (some->> (clojure.string/index-of history-line ";")
           inc
           (subs history-line)))

(defn split-clubbed-commands
  [command]
  (clojure.string/split command #"(&&|\|\|)"))

(defn git-commit-message?
  [command]
  (or 
    (re-find #"^git commit -m" command)
    (re-find #"^gc -m" command)
    (re-find #"^gc --message" command)
    (re-find #"^git commit --message" command)))

(defn commit-message-for
  [command]
  (last (re-find #"\s(-m|--message){1}\s?['\"](.*)[\"']" command)))

(defn commit-messages
  []
  (->> (file-read (str (System/getProperty "user.home") "/.zsh_history"))
       clojure.string/split-lines
       (map extract-command)
       (filter some?)
       (mapcat split-clubbed-commands)
       (map clojure.string/trim)
       (filter git-commit-message?)
       (map commit-message-for)
       (filter some?)))

(run! println (commit-messages))

(comment 
  (file-read "deps.edn")
  
  (clojure.string/split-lines (file-read (str (System/getProperty "user.home") "/.zsh_history")))

  (extract-command ": 1549283374:0);git diff HEAD..HEAD~1 --name-only")

  (git-commit-message? "git commit -m 'abcde'")
  
  (git-commit-message? "git commi-m 'abcde'")

  (git-commit-message? "git commit && git commi-m 'abcde'")

  (git-commit-message? "gc -m '' && git commi-m 'abcde'")

  (extract-single-command "abc&&def")

  (extract-single-command "abc && def")

  (extract-single-command "git commit || ggp")
  
  (commit-message-for "git commit -m 'shjdhd'")

  (commit-message-for "git commit --message 'dkjld'")

  (clojure.string/index-of "abd" ";")

  (clojure.string/split nil #"bleh"))
