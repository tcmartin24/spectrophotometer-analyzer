#include <math.h>

class LoopSinGenerator {
  public:
    LoopSinGenerator(int max, float rate);
    int getValue();
    float getRads();
    int getMax();
    float getRate();
  private:
    float _rads;
    int _value;
    int _max;
    float _rate;
};
