TOOL = ./testGeneration.bash

OBJS = $(SRCS:.txt=.kt)

SRCS = $(shell ls *.txt)

%.kt:%.txt
	$(TOOL) $< >  $@

all: $(OBJS)

clean:
	rm -f $(OBJS)

