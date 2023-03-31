#include "LoopSinGenerator.h"

LoopSinGenerator::LoopSinGenerator(int max, float rate) {
  float _rads = 0.0;
  int _max = max;
  float _rate = .01;
  int _value = 0;
}

int LoopSinGenerator::getValue() {
  _value = (int)(128 * sin(_rads) + 128);
  // _value = (int) (_max/2) * sin(_rads) + (_max/2);
  _rads+=.001;
  if (_rads > (2 * M_PI)) { _rads = 0; }
  return _value;
}

float LoopSinGenerator::getRads() {
  return _rads;
}

int LoopSinGenerator::getMax() {
  return _max;
}

float LoopSinGenerator::getRate() {
  return _rate;
}
