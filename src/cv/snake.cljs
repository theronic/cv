(ns cv.snake
  (:require [reagent.core :as r :refer [atom cursor]]))
            ;[cljs.pprint :as pprint]))

(enable-console-print!)

(def initial-state
  {:paused?      false
   :width        12
   :height       12
   :position     [6 8]
   :history      ()
   :pill-density 0.02
   :pills        #{}                                        ;[4 4] [5 8] ;; generate?
   :velocity     [1 0]                                     ;; random?
   :size         3
   :queue        [:left]
   :dead?        false
   :interval     250})

(defonce !state (atom initial-state))
(def !tick-interval (cursor !state [:interval]))
(def !queue (cursor !state [:queue]))
(def !paused? (cursor !state [:paused?]))
(def !dead? (cursor !state [:dead?]))

(def cell-colors
  {:head "gold"
   :worm "green"
   :pill "red"
   :wall "grey"})

(defn cell-view [x y type [dx dy]] ;; todo map
  [:div
   {:style (merge {:float            "left"
                   :clear            (if (zero? x) "left")
                   :width            "12px"
                   :height           "12px"
                   :background-color (get cell-colors type "rgba(0, 0, 50, 0.5")
                   ;:border-radius          (if (get cell-colors type) "0.24em" "0")
                   ; can we do xor here?
                   :margin           "1px"
                   :border-right     "1px solid #777"
                   :border-bottom    "1px solid #777"}
                  (if (= type :head) ;; todo compress into border-radius four
                    {:border-top-right-radius    (if (or (pos? dx) (neg? dy)) "24px")
                     :border-bottom-right-radius (if (or (pos? dx) (pos? dy)) "24px")
                     :border-bottom-left-radius  (if (or (neg? dx) (pos? dy)) "24px")
                     :border-top-left-radius     (if (or (neg? dx) (neg? dy)) "24px")}))}])

(defn handle-keys! [!state event] ;; ideally this should emit an event, not mutate state directly
  (let [key (.-keyCode event)]
    (case key
      32 (swap! !paused? not)                               ; spacebar
      13 (if @!dead? (reset! !state initial-state))         ; enter
      (when-let [dir (case key
                       37 :left
                       39 :right
                       nil)]
        (.preventDefault event)
        (swap! !queue conj dir))))
  nil)

(defn next-state [{:keys [position size velocity queue pills pill-density width height history] :as state}]
  (let [snake         (set (take size history))
        found-pill?   (pills position)
        [dx dy] velocity
        direction     (first queue)
        new-vel       (case direction                       ; 90-degree rotation matrices
                        :left [dy (- dx)]
                        :right [(- dy) dx]
                        [dx dy])
        new-pos       (mapv mod (map + new-vel position) [width height])
        pills         (if found-pill? (disj pills position) pills)
        missing-pills (- (* pill-density width height) (count pills))
        new-pills     (take missing-pills (repeatedly #(vector (rand-int width) (rand-int height))))]
    (assoc state
      :dead? (if (snake new-pos) true false)
      :queue (drop 1 queue)
      :velocity new-vel
      :history (take size (conj history position))
      :position new-pos
      :interval (- 250 (* 3 size))
      :pills (into pills new-pills)
      :size (if found-pill? (inc size) size))))

(defn world-view [{:as state :keys [pills size velocity width height history position]}]
  (let [worm (set (take size history))]
    [:div
     (for [y (range height)
           x (range width)]
       (let [pos       [x y]
             cell-type (cond
                         (= position pos) :head
                         (worm pos) :worm
                         (pills pos) :pill)]
         ^{:key [x y]}
         [cell-view x y cell-type velocity]))]))

(defn game-container [!state !tick-interval]
  (r/with-let [key-handler (partial handle-keys! !state)
               _           (js/window.addEventListener "keydown" key-handler)
               tick!       #(if (and (not @!dead?) (not @!paused?))
                              (swap! !state next-state))
               interval    (js/setInterval tick! @!tick-interval)]
    [world-view @!state]
    (finally
      (js/clearInterval interval)
      (js/window.removeEventListener "keydown" key-handler))))


(defn parent-snake-component []
  (let [size (cursor !state [:size])]
    [:div
     (if (:dead? @!state)
       [:div
        [:h2 "Game Over"]
        [:button {:on-click #(reset! !state initial-state)} "Play Again!"]]
       ^{:key (:interval @!state)} [game-container !state !tick-interval])
     [:div [:p "Score: " (- (:size @!state) (:size initial-state))]]
     [:div [:p
            [:button {:on-click #(swap! !state next-state)} "Tick!"] " "
            [:button {:on-click #(swap! size inc)} "Grow!"] " "
            [:button {:on-click #(swap! size dec)} "Shrink!"] " "
            ;[:button {:on-click #(swap! !tick-interval - 15)} "Faster"] " "
            ;[:button {:on-click #(swap! !tick-interval + 15)} "Slower"] " "
            [:button {:on-click #(swap! !paused? not)} (if @!paused? "Unpause" "Pause")]
            [:button {:on-click #(reset! !state initial-state)} "New Game"]]]]))
     ;[:pre {:style {:clear "left"}} "Game State: " (with-out-str (pprint/pprint @!state))]]))

(defn ^:export init []
  (r/render-component [parent-snake-component] (.getElementById js/document "container")))