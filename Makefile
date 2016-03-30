SRC=BigInteger.java
CLS=$(SRC:%.java=%.class)

.PHONY: all clear test

all: $(CLS) test

test: $(CLS)
	java $(CLS:%.class=%)

clean:
	rm -rf $(CLS)

%.class: %.java
	javac $*.java
