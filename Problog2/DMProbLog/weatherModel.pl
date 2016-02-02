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