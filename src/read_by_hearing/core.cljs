(ns read-by-hearing.core
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defonce app-state (atom {:text-grid [{:word "Irgendwo" :end-time 0.44}
                                      {:word "im" :end-time 0.57}
                                      {:word "Mittelmeer" :end-time 1.05}
                                      {:word "liegt" :end-time 1.33}
                                      {:word "eine" :end-time 1.52}
                                      {:word "Insel" :end-time 1.9886836799130783}
                                      {:word "von" :end-time 2.54}
                                      {:word "der" :end-time 2.64}
                                      {:word "viele" :end-time 2.98}
                                      {:word "Menschen" :end-time 3.3}
                                      {:word "glauben," :end-time 3.68}
                                      {:word "dass" :end-time 3.82}
                                      {:word "sie" :end-time 3.93}
                                      {:word "verzaubert" :end-time 4.48}
                                      {:word "sein" :end-time 4.79}
                                      {:word "könnte." :end-time 5.166705795701746}]
                          :position 0}))

(defn read-words
  "Returns a string of all words annotated with an end time less or equal than
   position. These are the words, that have been read already."
  [text-grid position]
  (->> text-grid
       (filter #(<= (:end-time %) position))
       (map :word)
       (str/join " ")
       (not-empty)))

(defn unread-words
  "Returns a string of all words annotated with an end time greater than
   position. These are the words, that have not been read until position."
  [text-grid position]
  (->> text-grid
       (filter #(> (:end-time %) position))
       (map :word)
       (str/join " ")
       (not-empty)))

(defn ensure-separated-unread
  "Returns unread with a leading \" \" when both exist. Thus they can be put
   together into the HTML document.

   Returns unread otherwise."
  [read unread]
  (if (and read unread)
    (str " " unread)
    unread))

(defn render-text-grid
  "Returns a component containing the currently read and unread words from the
   given text grid. The given position is used as current."
  [{:keys [text-grid position]}]
  (om/component
   (dom/p nil
          (let [read (read-words text-grid position)
                unread (unread-words text-grid position)]
            (dom/p nil
                   (dom/span #js {:className "read"} read)
                   (ensure-separated-unread read unread))))))


(om/root
 (fn [data owner]
   (render-text-grid data))
 app-state
 {:target (. js/document (getElementById "app"))})

(defn update-position!
  "Sets the position in the app state to the given value."
  [new-position]
  (swap! app-state #(assoc % :position new-position)))
