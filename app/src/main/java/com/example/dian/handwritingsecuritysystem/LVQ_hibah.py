# -*- coding: utf-8 -*-
"""
Created on Mon Jun  1 19:37:31 2020

@author: Steven
"""



import numpy as np
import matplotlib.pyplot as plt
from random import uniform


#Fungsi LVQ Fit
def lvq_fit(train, target, learn_rate, b, max_epoch):
    label, train_idx = np.unique(target, return_index=True)
    print("\nIsi label = ",label)
    print("\n\nIsi train idx = ",train_idx)
    weight = train[train_idx].astype(np.float64)
    print("\n\nIsi weight awal= ",weight)
    train = np.array([e for i, e in enumerate(zip(train, target)) if i not in train_idx])
    print("================================================================================")
    print("\n\nTrain hasil enumerate = ",train)
    train, target = train[:, 0], train[:, 1]
    epoch = 0

    while epoch < max_epoch:
        for i, x in enumerate(train):
            distance = [sum((w - x) ** 2) for w in weight]
            min = np.argmin(distance)
            sign = 1 if target[i] == label[min] else -1
            weight[min] += sign * learn_rate * (x - weight[min])

        learn_rate *= b
        epoch += 1

    return weight, label


#Fungsi LVQ Predict
def lvq_predict(x, weight):
    weight, label = weight
    d = [sum((w - x) ** 2) for w in weight]

    return label[np.argmin(d)]


# Pembacaan isi JSON dari file eksternal
import json

list_train = []

with open('nilai_feature.json', 'r') as f:
    fiturs_dict = json.load(f)
    for fitur in fiturs_dict:
        print("\n\nNAMA : ",fitur['nama'])
        # input nilai fitur kedalam list data training
        list_train.append( 
                 [
                        fitur['nilai_contrast'], 
                        fitur['nilai_skewness'], 
                        fitur['nilai_energy'], 
                        fitur['nilai_invariance']
    		     ]
                )

        print("\n\nTRAIN  = ",list_train)
        
        print("--------------------------------------------------------------------")

#list diubah kedalam bentuk array
train_array = np.array(list_train)
print("ARRAY  = ",train_array)
    

#target kelas masing-masing input
target = np.array([1, 1, 0, 1, 1])
weight = lvq_fit(train_array, target, learn_rate=.1, b=.5, max_epoch=10)
print("================================================================================")
print("\n\nIsi Weight akhir = ",weight)
output = lvq_predict([111.12, 0, 96922.0, 1074.0], weight)
test = uniform(train_array[:, 0].min(), train_array[:, 0].max()), uniform(train_array[:, 1].min(), train_array[:, 1].max())
print("================================================================================")
print("\n\nHasil klasifikasi, termasuk dalam kelas : ",output)


# Visualisasi dengan Plot
colors = 'rgbcmyk'

for x, label in zip(train_array, target):
    plt.plot(x[0], x[1], colors[label] + '.')

plt.plot(test[0], test[1], colors[output] + '^')




