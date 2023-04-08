(ns org.jakehoward.sdc.visualisation.app
  (:require [reagent.core :as r]
            [reagent.dom :as rd]))

(def example-layout
  {:x-min 0 :x-max 600 :y-min 0 :y-max 400
   :type :four-way
   :n->s {:name "A1" :lane-width 12 :speed-limit 100}
   :e->w {:name "A2" :lane-width 8 :speed-limit 80}})

(defn simulation->svg [{:keys [layout]}]
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
       [:rect {:width (* 2 ns-lane-width) :height y-max :fill "#333" :x (- centre-x ns-lane-width) :y 0}]
       [:rect {:width x-max :height (* 2 ew-lane-width) :fill "#444" :x 0 :y (- centre-y ew-lane-width)}]
       [:line {:x1 0 :y1 centre-y :x2 x-max :y2 centre-y :stroke "yellow"}]
       [:line {:x1 centre-x :y1 0 :x2 centre-x :y2 y-max :stroke "yellow"}]
       ;; [:rect {:width x-max :height y-max :fill "#222" :x 0 :y 0}]
       ]]]))


(defn visualisation []
  [:div
   (simulation->svg {:layout example-layout})])

(defn home-page []
  [:div
   [:h1 {:className "title"} "Four way junction"]
   [:div {:className "app-container"}
    [:div {:className "simulation"}
     [visualisation {:data :rocks}]]
    ]])

(defn init []
  (println "I'm a work in progress...")
  (rd/render [home-page] (.getElementById js/document "root")))
