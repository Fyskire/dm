t(_)::weather(sun,0) ; t(_)::weather(rain,0).
t(_)::weather(sun,T) ; t(_)::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(sun,Tprev).
t(_)::weather(sun,T) ; t(_)::weather(rain,T) :- integer(T), T>0, Tprev is T-1, weather(rain,Tprev).
