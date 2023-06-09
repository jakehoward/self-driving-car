(ns org.jakehoward.sdc.visualisation.app
  (:require [reagent.core :as r]
            [reagent.dom :as rd]
            [cljs.reader :as reader]))

;; --------
;; fixtures
;; --------
(def example-layout
  {:x-min 0 :x-max 600 :y-min 0 :y-max 400
   :type :four-way
   :n->s {:name "A1" :lane-width 12 :speed-limit 100}
   :e->w {:name "A2" :lane-width 8 :speed-limit 80}})

;; rough example of logic
;; - todo: centre in lane, put on specific road, give it a direction
(defn example-cars [layout]
  (let [vehicle-length 10
        initial-y   (- (:y-max layout) (inc vehicle-length))
        centre-x    (/ (:x-max layout) 2)
        x           (+ 2 (- centre-x (get-in layout [:n->s :lane-width])))
        time-slots  (dec initial-y)
        example-car {:id 1 :color "lightgreen" :length vehicle-length :width 7 :front-left {:x x :y 11}}]
    (->> (for [s (range time-slots)]
           [s [(assoc-in example-car [:front-left :y] (- initial-y s))]])
         (into {}))))

;; ---
;; app
;; ---

;; (defonce state (r/atom {:layout example-layout :vehicles (example-cars example-layout) :time-slot 0}))

(defonce state (r/atom nil))

(defonce interval (r/atom nil))

(defn simulation->svg [{:keys [layout vehicles]}]
  (let [x-max    (:x-max layout)
        y-max    (:y-max layout)
        centre-x (/ x-max 2)
        centre-y (/ y-max 2)
        width    700
        height   (Math/ceil (* (/ y-max x-max) width))
        ns-lane-width (get-in layout [:n->s :lane-width])
        ew-lane-width (get-in layout [:e->w :lane-width])]
    [:div {:className "scene"}
     [:svg {:viewBox (str "0 0 " x-max " " y-max) :width width :height height}
      [:g
       [:rect {:width x-max :height y-max :fill "#ccc" :x 0 :y 0}]
       [:rect {:width (* 2 ns-lane-width)
               :height y-max
               :fill "#333"
               :x (- centre-x ns-lane-width)
               :y 0}]
       [:rect {:width x-max
               :height (* 2 ew-lane-width)
               :fill "#444"
               :x 0
               :y (- centre-y ew-lane-width)}]
       [:g
        (->>
         vehicles
         (map
          (fn [v]
            [:rect
             {:width (:width v)
              :height (:length v)
              :fill (:color v)
              :x (get-in v [:front-left :x])
              :y (get-in v [:front-left :y])}]))
         flatten
         vec)]
       [:line {:x1 0 :y1 centre-y :x2 x-max :y2 centre-y :stroke "yellow"}]
       [:line {:x1 centre-x :y1 0 :x2 centre-x :y2 y-max :stroke "yellow"}]
       ]]]))


(defn visualisation [{:keys [layout vehicles]}]
  [:div
   (simulation->svg {:layout layout :vehicles (get vehicles (:time-slot @state))})])


(defn next-tick []
  (if (not @state)
    (println "Next tick called when there is no state...!")
    (swap! state (fn [s] (if (< (:time-slot s) (dec (count (:vehicles s))))
                           (update s :time-slot inc)
                           s)))))

(defn start [protocol]
  (-> (.fetch js/window (str
                         "http://localhost:8081/api/v1/simulation/junction/four-way?protocol="
                         protocol))
      (.then (fn [res] (.text res)))
      (.then
       (fn [body]
         (let [data (reader/read-string body)]
           (reset! state {:layout (:layout data)
                          :vehicles (:time-slot-to-vehicles data)
                          :time-slot 0})
           (when @interval (js/clearInterval @interval))
           (reset! interval (js/setInterval next-tick (:resolution-ms data))))))
      (.catch (fn [err] (println "Error in start: " (.-message err))))))

(defn control-pane []
  [:div
   [:button {:on-click (fn [] (start "example"))} "Start"]])

(defn home-page []
  [:div
   [:h1 {:className "title"} "Four way junction"]
   [:div {:className "app-container"}
    [:div {:className "simulation"}
     [control-pane]
     (when @state
       [visualisation @state])]
    ]])

(defn init []
  (println "I'm a work in progress...")
  (rd/render [home-page] (.getElementById js/document "root")))
