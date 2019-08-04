from __future__ import print_function

from subprocess import check_call
#import pydot
#import pydotplus


import time

import itertools

from sklearn.model_selection import train_test_split



import StringIO

import traceback

from sys import platform


import os
import csv
import numpy as np

from scipy import stats

import glob
from scipy import fftpack


from sklearn.datasets import make_blobs
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_samples, silhouette_score
from sklearn.semi_supervised import label_propagation

from sklearn.model_selection import cross_val_score

from sklearn import preprocessing

import matplotlib.pyplot as plt


from sklearn.metrics import confusion_matrix
from sklearn.model_selection import KFold
from sklearn.metrics import classification_report
from sklearn.metrics import accuracy_score


#from sklearn.lda import LDA
from sklearn.linear_model import LogisticRegression
from sklearn.neighbors import KNeighborsClassifier
from sklearn import svm
from sklearn import tree
from sklearn.naive_bayes import GaussianNB
from sklearn import preprocessing
from sklearn.ensemble import RandomForestClassifier
from sklearn.ensemble import AdaBoostClassifier

from scipy.spatial.distance import cdist

from datetime import datetime

import operator

import random

#from pcapng import FileScanner

##from pycopkmeans import *
##from constrained_kmeans import *
#from cop_kmeans import *



print ("Beginning Feature Analysis! This may take a few minutes!") 

###############################  K-means ###################

input_folder = "output" ################# INSERT FEATURE EXTRACTED CSV FILE HERE!!!!! ######
print ("************************")

if platform == 'win32':
    pathSep = "\\"
    pathSepD = "\\"
else:
    pathSep = "/"
    pathSepD = "/"
    
window_size = 96
shift_amount = 32
    
input_file =  "features_" + "win_" + str(window_size) + "_shift_" + str(shift_amount) + ".csv"

print (input_file)

X=np.array(list(csv.reader((line.replace('\0','') for line in open(os.getcwd() + pathSepD + input_folder + pathSepD + input_file, "rb")), delimiter="," )))
    
print (X)
true_user = []
true_time = []
true_labels = []



################ Remove None ##################

#indexes = []
#
#for a in range(len(X)):
#    if ('None' in X[a][len(X[0])-2]):
#        indexes.append(a)
#    
#X = np.delete(X, indexes, axis=0)


for a in range(3):
    np.random.shuffle(X)
    #np.take(X,np.random.permutation(X.shape[0]),axis=0,out=X)


true_labels = X[:,len(X[0])-2]
true_user = X[:,len(X[0])-1]
true_time = X[:,0:2]
X = np.delete(X, len(X[0])-1, 1)
X = np.delete(X, len(X[0])-1, 1)
X = np.delete(X, 0, 1)
X = np.delete(X, 0, 1)
X = X.astype(np.float)
#X = preprocessing.scale(X)

    
# Cross- Validation Schemes
# Cross- Validation Schemes
kf = KFold(5, shuffle=False)
   
# Classifier Selection
#clf = LDA()
#clf = LogisticRegression()
#clf = KNeighborsClassifier(n_neighbors=3)
#clf = svm.SVC(kernel="linear")
#clf = svm.SVC(kernel="rbf")
#clf = tree.DecisionTreeClassifier()
#clf = GaussianNB()

clf = RandomForestClassifier()


#clf = AdaBoostClassifier(n_estimators=1000)
#clf = AdaBoostClassifier(tree.DecisionTreeClassifier())



# Needed for RBF SVM
#features = preprocessing.scale(features)

X_train, X_test, Y_train, Y_test = train_test_split(X, true_labels, test_size=0.0)


allPredictions = []
allClassifications = []

#print(cluster_labels)
#cluster_labels = np.array(cluster_labels)
#print(cluster_labels)

for train, test in kf.split(Y_train):
    clf.fit(X_train[train], Y_train[train])
    predictions = clf.predict(X_train[test])
    allPredictions.append( predictions)
    allClassifications.append(Y_train[test])
    
#print(avg(mse))
    

allPredictions = np.concatenate(allPredictions)
allClassifications = np.concatenate(allClassifications) 
   
print ('Get Results')

print(classification_report(allClassifications,allPredictions))

print("Accuracy: " + ("%.6f"%accuracy_score(allClassifications,allPredictions)))

cnf_matrix = confusion_matrix(allClassifications,allPredictions)

plot_confusion_matrix(cnf_matrix, classes=[], normalize=True, title='Normalized confusion matrix')