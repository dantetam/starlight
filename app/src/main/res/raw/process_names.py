# Copyright 2015 The TensorFlow Authors. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ==============================================================================

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import argparse
import sys
import os

FLAGS = None

def organizeCensusNames(fname, outputMale, outputFemale):
  # print(os.listdir("./"))
  with open(fname, encoding="latin-1") as f:
    for line in f:
      tokens = line.strip().split(" ")
      with open(outputMale, 'a') as f2:
        f2.write(tokens[1].strip() + "\n")
      with open(outputFemale, 'a') as f2:
        f2.write(tokens[2].strip() + "\n")
      
organizeCensusNames("./app/src/main/res/raw/common_names_raw.txt", "./app/src/main/res/raw/male_names.txt", "./app/src/main/res/raw/female_names.txt")
 
