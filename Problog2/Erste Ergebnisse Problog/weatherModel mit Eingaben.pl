%---WeatherModel
t(_)::weather(sun,0).
1::weather(rain,T) :- \+weather(sun,T).
t(_)::weather(sun,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).
t(_)::weather(sun,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).
t(_)::weather(windy,0).
t(_)::weather(windy,T) :- integer(T), T>0, Tprev is T-1, weather(windy,Tprev).
t(_)::weather(windy,T) :- integer(T), T>0, Tprev is T-1, \+weather(windy,Tprev).
1::weather(cold,T) :- \+weather(warm,T).
t(_)::weather(warm,T)  :- integer(T), T>=0, weather(sun,T).
t(_)::weather(warm,T) :- integer(T), T>=0, weather(rain,T).

%Idee: Nehme von nah zusammenliegenden Wetterstationen die gleichen Tage (hier 01.-07.02.2014, unten je die Daten 17-24), lerne auf ihnen und erzeuge dann Aussage/Anfrage für die 8 Tage 4.-11.2. (unten queries T=4 bis T=11).

%Weitere Ideen: Modell erweitern für die Abfrage, ob Regenschirm benötigt wird.


%Wetterstationen: (ungefähr gleiche Geo-Breite/-Hoehe)
Nr	Name			GBreite	GHoehe	Hoehe	
10609	Trier-Petrisberg	49,75	6,667	265	DWD
10616	Hahn			49,95	7,267	502	DWD
10637	Frankfurt/M-Flughafen	50,05	8,6	111	DWD
10641	Offenbach-Wetterpark	50,083	8,783	119	DWD

%--------------------------------------------------------------------------------------------
%---------Ergebnisse für die obigen 4 Wetterstationen (nah aber nicht auf gleicher Höhe)
%iterations=2
Fact			Location	Probability
weather(sun,0)		1:7		0.94980206
weather(sun,T)		3:7		0.23734071
weather(sun,T)		4:7		0.54697669
weather(warm,T)		9:7		0.57044022
weather(warm,T)		10:7		0.84114062
weather(windy,0)	5:7		0.56054715
weather(windy,T)	6:7		0.55959625
weather(windy,T)	7:7		0.21874618

%Stats: score=-87.90728263995975, iterations=1034
Fact			Location	Probability
weather(sun,0)		1:7		0
weather(sun,T)		3:7		0.24269656
weather(sun,T)		4:7		0.99324719
weather(warm,T)		9:7		0.53960075
weather(warm,T)		10:7		0.52473212
weather(windy,0)	5:7 		0.66319567
weather(windy,T)	6:7		0.41525578
weather(windy,T)	7:7		0.4630771
%---Zugehöriges Model mit entsprechenden Probabilities (4 Stationen)
0::weather(sun,0).
1::weather(rain,T) :- \+weather(sun,T).
0.24269656::weather(sun,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).
0.99324719::weather(sun,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).
0.66319567::weather(windy,0).
0.41525578::weather(windy,T) :- integer(T), T>0, Tprev is T-1, weather(windy,Tprev).
0.4630771::weather(windy,T) :- integer(T), T>0, Tprev is T-1, \+weather(windy,Tprev).
1::weather(cold,T) :- \+weather(warm,T).
0.53960075::weather(warm,T)  :- integer(T), T>=0, weather(sun,T).
0.52473212::weather(warm,T) :- integer(T), T>=0, weather(rain,T).

%--------------------------------------------------------------------------------------------
%---------Ergebnisse für die 2 Wetterstationen Frankfurt und Offenbach (liegen sehr nah und auf gleicher Höhe
$---Stats: score=-36.19065171677502, iterations=1890
Fact			Location	Probability
weather(sun,0)		1:7		0.99999932
weather(sun,T)		3:7		0.77887766
weather(sun,T)		4:7		0.08671332
weather(warm,T)		9:7		0.55334763
weather(warm,T)		10:7		0.52900497
weather(windy,0)	5:7		0.4993991
weather(windy,T)	6:7		0.88851898
weather(windy,T)	7:7		0.43013988
%---Zugehöriges Model mit entsprechenden Probabilities (2 Stationen)
0.99999932::weather(sun,0).
1::weather(rain,T) :- \+weather(sun,T).
0.77887766::weather(sun,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).
0.08671332::weather(sun,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).
0.4993991::weather(windy,0).
0.88851898::weather(windy,T) :- integer(T), T>0, Tprev is T-1, weather(windy,Tprev).
0.43013988::weather(windy,T) :- integer(T), T>0, Tprev is T-1, \+weather(windy,Tprev).
1::weather(cold,T) :- \+weather(warm,T).
0.55334763::weather(warm,T)  :- integer(T), T>=0, weather(sun,T).
0.52900497::weather(warm,T) :- integer(T), T>=0, weather(rain,T).

%-------------------------------------
%---Zugehörige Abfragen:
query(weather(warm,4)).
query(weather(warm,5)).
query(weather(warm,6)).
query(weather(warm,7)).
query(weather(warm,8)).
query(weather(warm,9)).
query(weather(warm,10)).
query(weather(warm,11)).

query(weather(sun,4)).
query(weather(sun,5)).
query(weather(sun,6)).
query(weather(sun,7)).
query(weather(sun,8)).
query(weather(sun,9)).
query(weather(sun,10)).
query(weather(sun,11)).

query(weather(windy,4)).
query(weather(windy,5)).
query(weather(windy,6)).
query(weather(windy,7)).
query(weather(windy,8)).
query(weather(windy,9)).
query(weather(windy,10)).
query(weather(windy,11)).

%----------------------------------------------

%Wetterdaten jeweils vom 1.-7.02.2014 (Start je 15.01. = T=0; also 15.01.+18Tage=01.02. also T=17)
-----
%10609	Trier-Petrisberg
evidence(weather(sun,17),true).
evidence(weather(windy,17),true).
evidence(weather(cold,17),true).
evidence(weather(sun,18),true).
evidence(weather(windy,18),false).
evidence(weather(cold,18),true).
evidence(weather(sun,19),true).
evidence(weather(windy,19),false).
evidence(weather(cold,19),true).
evidence(weather(sun,20),true).
evidence(weather(windy,20),false).
evidence(weather(cold,20),true).
evidence(weather(sun,21),true).
evidence(weather(windy,21),true).
evidence(weather(cold,21),true).
evidence(weather(rain,22),true).
evidence(weather(windy,22),true).
evidence(weather(cold,22),true).
evidence(weather(rain,23),true).
evidence(weather(windy,23),true).
evidence(weather(cold,23),true).
evidence(weather(sun,24),true).
evidence(weather(windy,24),true).
evidence(weather(cold,24),true).
-----
%10616	Hahn
evidence(weather(sun,17),true).
evidence(weather(windy,17),false).
evidence(weather(cold,17),true).
evidence(weather(sun,18),true).
evidence(weather(windy,18),false).
evidence(weather(cold,18),true).
evidence(weather(sun,19),true).
evidence(weather(windy,19),false).
evidence(weather(cold,19),true).
evidence(weather(sun,20),true).
evidence(weather(windy,20),false).
evidence(weather(cold,20),true).
evidence(weather(sun,21),true).
evidence(weather(windy,21),false).
evidence(weather(cold,21),true).
evidence(weather(rain,22),true).
evidence(weather(windy,22),true).
evidence(weather(cold,22),true).
evidence(weather(rain,23),true).
evidence(weather(windy,23),true).
evidence(weather(cold,23),true).
evidence(weather(sun,24),true).
evidence(weather(windy,24),true).
evidence(weather(cold,24),true).
-----
%10637	Frankfurt/M-Flughafen
evidence(weather(sun,17),true).
evidence(weather(windy,17),false).
evidence(weather(cold,17),true).
evidence(weather(sun,18),true).
evidence(weather(windy,18),false).
evidence(weather(cold,18),true).
evidence(weather(sun,19),true).
evidence(weather(windy,19),false).
evidence(weather(cold,19),true).
evidence(weather(sun,20),true).
evidence(weather(windy,20),false).
evidence(weather(cold,20),true).
evidence(weather(sun,21),true).
evidence(weather(windy,21),false).
evidence(weather(cold,21),true).
evidence(weather(sun,22),true).
evidence(weather(windy,22),false).
evidence(weather(cold,22),true).
evidence(weather(sun,23),true).
evidence(weather(windy,23),true).
evidence(weather(cold,23),true).
evidence(weather(rain,24),true).
evidence(weather(windy,24),false).
evidence(weather(cold,24),true).
-----
%10641	Offenbach-Wetterpark
evidence(weather(sun,17),true).
evidence(weather(windy,17),false).
evidence(weather(cold,17),true).
evidence(weather(sun,18),true).
evidence(weather(windy,18),false).
evidence(weather(cold,18),true).
evidence(weather(sun,19),true).
evidence(weather(windy,19),false).
evidence(weather(cold,19),true).
evidence(weather(sun,20),true).
evidence(weather(windy,20),false).
evidence(weather(cold,20),true).
evidence(weather(sun,21),true).
evidence(weather(windy,21),false).
evidence(weather(cold,21),true).
evidence(weather(sun,22),true).
evidence(weather(windy,22),false).
evidence(weather(cold,22),true).
evidence(weather(sun,23),true).
evidence(weather(windy,23),true).
evidence(weather(cold,23),true).
evidence(weather(rain,24),true).
evidence(weather(windy,24),false).
evidence(weather(cold,24),true).



%----------anderes, was ausprobiert wurde:

0::weather(sun,0) ; 1::weather(rain,0).
0.59794918::weather(sun,T) ; 0.24678244::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).
0.19063373::weather(sun,T) ; 0.38182769::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).


query(weather(T,5)).
%Ergebniss:
weather(rain,5)	5:7	
 0.0589802
weather(sun,5)	5:7	
 0.0824212

%---------------------------------
1::weather(sun,0) ; 0::weather(rain,0).
0.63079139::weather(sun,T) ; 0.06056884::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).
0.31835306::weather(sun,T) ; 0.18879488::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).

query(weather(sun,0)).
query(weather(sun,1)).
query(weather(sun,2)).
query(weather(sun,3)).
query(weather(sun,4)).
query(weather(sun,5)).
query(weather(sun,6)).

weather(sun,0)	5:7	
 1
weather(sun,1)	6:7	
 0.63079139
weather(sun,2)	7:7	
 0.41718005
weather(sun,3)	8:7	
 0.27895707
weather(sun,4)	9:7	
 0.18699152
weather(sun,5)	10:7	
 0.12541356
weather(sun,6)	11:7	
 0.084124

