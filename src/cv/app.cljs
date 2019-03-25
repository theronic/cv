(ns cv.app
  (:require [reagent.core :as reagent :refer [atom]]
            [cv.snake :as snake]
            [cv.chessmate :as chess]
            [cljs-time.core :as t]
            [cljs-time.format :as tf]))

(def font "Computer Modern Serif, serif")

(enable-console-print!)

(defn deep-merge [v & vs]
  (letfn [(rec-merge [v1 v2]
            (if (and (map? v1) (map? v2))
              (merge-with deep-merge v1 v2)
              v2))]                                         ; was v2
    (if (some identity vs)
      (reduce #(rec-merge %1 %2) v vs)
      v)))

(defn element [elem opts & args]
  (into [elem (if (map? opts) opts {})]
        (if (map? opts)
          args
          (cons opts args))))

(defn h1 [opts & args]
  (element :h1 (deep-merge {:style {:margin       0
                                    :padding      0
                                    :font-weight  "bold"
                                    :font-variant "small-caps"}} opts) args))

;(defn h1 [opts & args]
;  (into [:h1 {:style {:font-variant "small-caps"}}] args))

(defn h2 [& args]
  (into [:h2
         {:style {:font-variant "small-caps"
                  :margin       "0.31em 0"}}]
        args))

(defn h3 [& args]
  (into [:h3 {:style {:font-variant "small-caps"
                      :margin "0"}}] args))


(defn section-heading [& body]
  (into [:h2 {:style        {
                             :margin "0.62em 0"
                             :padding 0
                             :font-variant "small-caps"}}]
        body))

(defn chessmate []
  [:div
   [:h3 "Chessmate"]                                            ;" goes here todo animate"
   ;[cv.chessmate/pieces]])
   [chess/chess-board chess/initial-board]])

   ;[:span chess/initial-board]])

; we're going to need an auto-playing demo version of snake, and a version that kicks in on click to mount event listeners.

(defn snake-view [context]
  [:div
   [h2
    [:a {:href "https://theronic.github.io/cljs-snake/"}
     (:title context)]]
   [snake/parent-snake-component]])

(defmulti render :context)

(defmethod render :default [ctx]
  [:div "default render method here"])

(def cv-data
  {:contact    {:full-name     "Petrus Gerrit Theron"
                :url           "http://petrustheron.com/"
                :phone         "+27 (0) 76 338 1792"
                :twitter       "PetrusTheron"
                :github        "theronic"
                :stackoverflow "theronic"
                :email         "petrus@petrus.co.za"}
   :projects   [{:title     "Snake in 100 Lines of Code"
                 :url       "https://theronic.github.io/cljs-snake/"
                 ; how to handle lifecycle? we can attach event listeners on load/unload
                 :component snake-view}
                ;{:name      "Chessmate"
                ; :interval (t/interval (t/local-date 2005 1 1)
                ;                       (t/local-date 2005 6 1))
                ; :component chessmate}
                {:name "Test"}]
   :education  [{:school   "Stellenbosch University"
                 :interval (t/interval (t/local-date 2006 1 1)
                                       (t/local-date 2010 12 1))
                 :degree   "BEng (Hons.) Electronic & Electrical Engineering with Computer Science"}]
   :experience [;{:from "" :to "Current"}
                {:position "Embedded Engineer"
                 :company  "BlueNova Energy Systems"
                 :url      "http://www.bluenova.co.za/"
                 :interval (t/local-date 2018 10 1)
                 ;(t/interval (t/local-date 2018 10 1)
                 ;            (t/local-date 2020 1 1))
                 :tags     [:rust :clojure :clojurescript :datomic :mqtt :iot :firmware :embeddded :monitoring]
                 :items    ["Remote monitoring and over-the-air firmware updates for high-current LiFeYPO₄ batteries."]}
                {:position "Founder & Lead Engineer"
                 :url      "http://wallfly.app/"
                 :company  "Wallfly: Smart Volume Control"
                 :interval (t/interval (t/local-date 2018 3 1)
                                       (t/local-date 2018 8 31)) ;; day?
                 :tags     [:ios :swift :c :clojure :native :mobile :sonic :acoustic]
                 :items    ["I took five months of unpaid leave to build a smart volume control system called Wallfly. Wallfly builds a dynamic acoustic sensor network to do automatic gain control at events, festivals and weddings. Auto-calibrates with ultrasound. Compensates for voice activity detection. Implemented on iOS, Web and OSX."]}
                {:position "Interim CTO"
                 :company  "JaSure"
                 :url      "https://jasure.com/"
                 :interval (t/interval (t/local-date 2017 9 1)
                                       (t/local-date 2018 2 28))
                 :tags     [:clojure :react-native :datomic :branding :domain :early-stage]
                 :items    ["Named company. Acquired jasure.com domain. Oversaw corporate identity with Hanno van Zyl (hanno.co.za)."
                            "Designed and deployed Datomic data model to be financially-auditable"
                            "Designed, mocked, prototyped and shipped React Native iOS app to App Store"]}
                {:position "Chief Technology Officer"
                 :company  "weFix Repair Specialists"
                 :url      "http://www.wefix.co.za/"
                 :interval (t/interval (t/local-date 2015 12 1)
                                       (t/local-date 2017 8 31))
                 :tags     [:asp.net-mvc :python :postgres :sql-server :c#]
                 :items    ["Wrote the software that runs weFix. Sold to weFix. Product development lead. Engineering lead. System design. Implementation. Getting weFix off the legacy systems I built onto greenfield systems that will scale into the future."]}
                {:position "Founder & CEO"
                 :company  "MyNames Domain Management"
                 :url      "http://www.mynames.co.za/"      ;; defunct
                 :interval (t/interval (t/local-date 2013 6 1)
                                       (t/local-date 2015 12 31))
                 :tags     [:python :domains :epp :postgres :salt-stack :devops :docker]
                 :items    ["Built an EPP domain registrar from scratch to do painless .CO.ZA. domain management. Early user of Docker."]}
                {:position "Founder & Lead Developer"
                 :company  "Krit.com - Mobile Customer Feedback"
                 :url      "http://krit.com/"               ;; defunct
                 :interval (t/interval (t/local-date 2011 1 1)
                                       (t/local-date 2013 1 31))
                 :tags     [:c# :asp.net-mvc :mobile :feedback :retail]
                 :items    ["Mobile customer feedback tool for the retail and restaurant sector. Let your customers reach you directly and privately from their phones."]}
                {:position "Embedded Developer (Contractor)"
                 :company  "wiWallet (now wiGroup)"
                 :url      "http://wigroup.co.za/"
                 :interval (t/interval (t/local-date 2008 8 1)
                                       (t/local-date 2008 9 31))
                 :tags     [:c :c++ :windows-ce]
                 :items    ["wiWallet has since been acquired. Implemented proprietary XML protocol over TCP/IP + SSL on Windows CE using a state machine."]}
                {:position "Lead Web Developer"
                 :company  "Rhythm Online Music Store"
                 :url      "http://rhythmmusicstore.com/"
                 :interval (t/interval (t/local-date 2006 6 1)
                                       (t/local-date 2012 12 31))
                 :tags     [:vb-script :c# :sql-server :ecommerce]
                 :items    ["Built an online music store from scratch before iTunes or Facebook became a thing in 2007. Handled credit card transaction and served MP3 downloads. Lots of database tuning. Designer to sysadmin, to devops."]}]})




(defn grid [opts & args]
  (into
    [:div {:style (deep-merge {:display             "grid"
                               :gridTemplateColumns "repeat(3, 1fr)"
                               :gridGap             "10px"}
                               ;:gridAutoRows        "minmax(100px, auto)"}
                              (if (map? opts) opts {}))}]
    (if (map? opts)
      args
      (cons opts args))))

(defn container [& args]
  (into [:div] args)
  ;(apply grid args)
  #_(into [:div {:style {} #_{:display "flex"}} args]))

(defn row [& args]
  (into [:div {:style {:flex-direction "row"}}] args))

;; yeah! my CV should have a game on it.
; how about snake? Yeah, that'd be dope...

(def custom-formatter (tf/formatter "MMM ''yy"))

(defn format-interval [interval]
  [:span {:style {
                  :font-family font
                  :font-style  "bold"}}
   (if (t/interval? interval)
     (let [start (t/start interval)
           end   (t/end interval)]
       ;(pr-str (if start (t/year start)) "-" (if end (t/year end)))
       (list
         (if start (tf/unparse custom-formatter start))
         " – "
         (if end
           (tf/unparse custom-formatter end))
         [:br] "(" (t/in-months interval) " months)"))
     (list
       (tf/unparse custom-formatter interval)
       " – Present"
       [:br]
       "(" (t/in-months (t/interval interval (t/now))) " months)"))])



(defn grid-template [styles & body]
  (into [:div {:style (deep-merge {:display "grid"
;                                   :gridTemplate "200px auto"
                                   :gridAutoFlow "column"} styles)}]
        body))

; can we combine the heading and columns into one thing?

(defn project-card [{:as project :keys [component]}]
  [:div
   {:style {
            ;:border-radius "4px"
            ;:width         "300px"
            ;:box-shadow    "0 1px 3px 0 #d4d4d5, 0 0 0 1px #d4d4d5"
            ;:border-right "1px solid #d3d3d5"
            :padding       "0.62em 1em"}}
            ;:flex-grow 1
            ;:margin        "0.62em 1em"}}
   [(or component :div) project]])

(defn section-container [& body]
  (into [container
         {:style {:background-color "rgba(0,0,0, 0.4)"
                  :text-shadow "1px 1px black"
                  ;:color            "white"
                  :padding          "1em"
                  :border-radius    "0.62em"}}] body))

(defn project-section [projects]
  [section-container
   ;[grid {:style {:width "300px"}}]
    ;[section-heading "Selected Work"]]
   ;{:style {:flex-direction "row"}}
   (for [project projects]
     [project-card project])])
   ;[grid-template
   ; {:gridTemplate  "1fr 1fr 1fr"
   ;  :gridGap       "1em"
   ;  :box-shadow    "0 1px 3px 0 #d4d4d5, 0 0 0 1px #d4d4d5"
   ;  ;:border        "1px solid #d4d"
   ;  :border-radius "1em"}
   ; (for [project projects]
   ;   [project-card project])]])


(defn xp-sub-item [{:as xp :keys [url interval items company position]}]
  [:div
   ;[:div
   ; {:style {:grid-area "dates"}}
   ; ;{:style {:grid-column 1
   ; ;         :grid-row 1}}
   ; (format-interval interval)]
   [:div
    {:style {:grid-area "main"}}
    ; ok, we care about role @ company, outcome, description, skills? responsibilities. outcome.
    [grid-template
     {:grid-template-columns "1fr 5fr"
      :grid-gap      "1em"}
     [:div
      {:style {:margin "0.5em 0"}}
      (format-interval interval)]
     [:div
      [:h2
       {:style {:margin 0}}
       (if url
         [:a {:href url} company]
         company)
       [:br]
       [:small position]]
      (for [item items]
        [:p item])]]]])

(defn edu-sub-item [{:as xp :keys [school degree interval items]}]
  [:div
   ;[:div
   ; {:style {:grid-area "dates"}}
   ; ;{:style {:grid-column 1
   ; ;         :grid-row 1}}
   ; (format-interval interval)]
   [:div
    {:style {:grid-area "main"}}
    ; ok, we care about role @ company, outcome, description, skills? responsibilities. outcome.
    [grid-template
     {:gridTemplate "1fr 4fr"
      :gridGap      "1em"}
     [:div
      {:style {:margin "0.5em 0"}}
      (format-interval interval)]
     [:div
      [:h2
       {:style {:margin 0}}
       school [:br]
       [:small degree]]
      (for [item items]
        [:p item])]]]])

(defn general-section [title data]
  [section-container
   ;{:style {:display "grid"}}
   #_{:style {:display             "grid"
              :gridTemplateColumns "200px auto"
              :gridTemplateAreas   "\"head head\" \"dates main\""}}
   #_{:style {:width "400px"}}
   [:div #_{:style {:gridArea "head"}}
    [h2 title]]
   (for [d data
         :let [{:keys [interval]} d]]
     [grid-template
      {:gridTemplate "1fr 4fr"
       :gridGap "1em"}
      [:div
       ;{:style {:grid-area "dates"}}
       ;{:style {:grid-column 1
       ;         :grid-row 1}}
       (format-interval interval)]
      [:div
       ;{:style {:grid-area "main"}}
       (pr-str d)]])
   #_(for [ed data]
       [xp-sub-item ed])])

;(defn education-section [{:as edu, :keys [interval]}]
;  [section-container
;   [:div
;    [:div
;     {:style {:display             "grid"
;              :gridTemplateColumns "200px auto"
;              :gridTemplateAreas   "\"head head\" \"dates main\""}}
;     #_{:style {:width "400px"}}
;     [:div {:style {:gridArea "head"}} [h2 "Education"]]
;     [:div
;      {:style {:grid-area "dates"}}
;      ;{:style {:grid-column 1
;      ;         :grid-row 1}}
;      (format-interval interval)]
;     [:div
;      {:style {:grid-area "main"}}
;      [h2 (:school edu)]
;      (pr-str edu)]
;     #_(for [ed edu]
;         [xp-sub-item ed])]]])

(defn education-section [xps]
  [section-container
   [:div [h2 "Education"]]
   (for [xp xps]
     [edu-sub-item xp])])

(defn experience-section [xps]
  [section-container
   [:div [h2 "Experience"]]
   (for [xp xps]
     [xp-sub-item xp])])

;; I'm so so tired. Tired of this life? Tired of not having a job. Not having an income.
; So, I'm working to fix that. To fix that, you first need to fix your behaviour.
; Don't negotiate so hard.

(defn section [& args]
  (into [:div {:style {:display             "grid"
                       :gridTemplateColumns "200px auto"}} args]))

; relative vertical timeline?
; specify height, calculate min/max, then minus margins

(defn label [& body]
  (into [:strong {:style {:font-family font}}] body))

(defn contact-section [{:as contact :keys [phone email github stackoverflow twitter]}]
  [:div
   [:h2 "Contact Details"] ;; relative heading would be nice
   [:div {:style {:display "grid"
                  :grit-template-columns "1fr 3fr"
                  :align-items "end"}}
    [:div
     [label "Phone:"] " " phone]
    [:div
     [label "Email:"] " " email]
    [:div
     [label "Twitter:"] " " [:a {:href (str "https://twitter.com/" twitter)} "@" twitter]]
    [:div
     [label "GitHub:"] " " [:a {:href (str "https://github.com/" github)} github]]
    [:div
     [label "Stack Overflow: "]
     [:a {:href "https://stackoverflow.com/users/198927/petrus-theron"}
      [:img {:src "https://stackoverflow.com/users/flair/198927.png?theme=dark"}]]]]])

(defn profile-photo [contact]
  [:div
   ; now to put snake under image
   (let [image-url "https://s3-eu-west-1.amazonaws.com/petrus-blog/petrus-louis-wedding-big.jpg"]
     [:a {:href image-url}
      [:img {:src   image-url
             :style {:border-radius "0.62em"}
             :width "100%"}]])
   [h2 "CTO for Hire"]
   [contact-section contact]])
   ;[:div [snake-view]]])

(defn cv-view
  [{:as cv :keys [contact
                  url
                  projects education experience]}]
  [:div
   {:style {:margin "0.62em 1em"}}
   [h1
    (if-let [url (:url contact)]
      [:a {:href url} (:full-name contact)]
      (:full-name contact))
    " "
    [:small "Curriculum Vitae"]]
   [:div
    {:style {:display               "grid"
             :grid-gap "1em"
             :grid-template-columns "2fr 5fr"}}
    ;{:style {:display "grid"}}
    [profile-photo contact]
    [:div {:style {:display  "grid"
                   :grid-gap "1em"}}
     ;:grid-template-rows "200px auto"}}
     ;:gridTemplateAreas   "\"head head\" \"dates main\""}}
     [project-section projects]
     [education-section education]
     ;[general-section "Education" education]
     [experience-section experience]
     [section-container "Scroll down :)"]]]])

(defn parent-component []
  [:div
   {:style {:font-family         font
            :max-width "1170px"
            ;:min-height "12000px"
            :margin "0 auto"
            ;:min-height "4000px"
            ;:background-image    "url('https://s3-eu-west-1.amazonaws.com/petrus-blog/petrus-louis-wedding-big.jpg')" ;"url('https://s3-eu-west-1.amazonaws.com/petrus-blog/petrus-louis-wedding-big.jpg')"
            ;:margin              0
            :padding             0}}

   [:style {:type "text/css"}
    "@media screen { body {
      background-color: #1f1f1d; font-size: 1.1em;
      color: #fffce6;
      }}
    a { color: gold;}
    a:visited { color: orange; }
    h1, h2, h3, h4 { font-weight: normal; }
    p {margin: 0.62em 0; line-height: 1.31em; font-size: 1.15em; }
    @media print { .hideprint { display: none; } }"]

            ;:background-position "top right"}}
   [:a {:href "https://github.com/theronic/cv"
        :title "View CV Source"}
    [:div.curl.hideprint]]
    ;[:div {:style {:width "120px"
    ;               :height "120px"
    ;               :background-image "url('code.png')"}}]]
   [:div.hideprint
    {:style {:position "absolute"
             :z-index -100
             :top 0
             :left 0
             ;:width "100%"
             :display "block"
             :min-width "99vw" ; 3628px
             :min-height "396vh"
             ;:height "12866px"
             :background-image "url('bg.JPG')"
             :background-size "cover"
             :background-position "5% 98%"
             ;:image-orientation "from-image"                     ;
             :background-repeat "no-repeat"}}]                    ;}}] ;"url('https://s3-eu-west-1.amazonaws.com/petrus-blog/petrus-louis-wedding-big.jpg')"}}]
   [:div {:style {
                  ;:width  "960px"
                  ;:display "grid"
                  ;:grid-template-columns "1fr 1fr"
                  :margin "0 auto"}}
    [:div [cv-view cv-data]]]])

(defn mount! []
  (reagent/render-component [parent-component]
                            (.getElementById js/document "container")))

(defn init! []
  (defonce inited (do (snake/init)))
  (mount!))

(defonce the-app (init!))