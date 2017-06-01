(ns ^:figwheel-always precept.draw.core
  (:require-macros [secretary.core :refer [defroute]])
  (:require [goog.events :as events]
            [reagent.core :as reagent]
            [secretary.core :as secretary]
            [precept.core :refer [start! then]]
            [precept.draw.facts :refer [todo visibility-filter]]
            [precept.draw.rules :refer [app-session]]
            [precept.draw.schema :refer [db-schema]]
            [precept.draw.views])
  (:import [goog History]
           [goog.history EventType]))

(enable-console-print!)

(defroute "/" [] (then (visibility-filter :all)))

(defroute "/:filter" [filter] (then (visibility-filter (keyword filter))))

(def history
  (doto (History.)
    (events/listen EventType.NAVIGATE (fn [event] (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

(defn mount-components []
  (reagent/render [precept.draw.views/app] (.getElementById js/document "app")))

(def facts (into (todo "Hi") (todo "there!")))

(defn mouse-move [e]
  [[:transient :mouse/x (.-clientX e)]
   [:transient :mouse/y (.-clientY e)]])

(defn mouse-down [e]
  [:transient :mouse/down e])

(defn mouse-up [e]
  [:transient :mouse/up e])

(defn ^:export main []
  (start! {:session app-session :facts facts})
  (doto js/window
    (.addEventListener "mousemove" #(then (mouse-move %)))
    (.addEventListener "mousedown" #(then (mouse-down %)))
    (.addEventListener "mouseup" #(then (mouse-up %))))
  (mount-components))
