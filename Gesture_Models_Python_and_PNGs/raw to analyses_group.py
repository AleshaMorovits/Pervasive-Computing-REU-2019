#%%
from __future__ import print_function

from subprocess import check_call
#import pydot
#import pydotplus
#import weka.core.jvm as jvm

#from weka.classifiers import *

import time
from scipy import signal
import itertools

#from imblearn.over_sampling import RandomOverSampler
#from imblearn.under_sampling import RandomUnderSampler


#from weka.core.converters import Loader


import StringIO

import traceback

from sys import platform

#from weka.core.classes import Random

import os
import csv
import numpy as np

from scipy import stats

import glob
from scipy import fftpack

from sklearn import preprocessing

from sklearn.datasets import make_blobs
from sklearn.cluster import KMeans
from sklearn.metrics import silhouette_samples, silhouette_score
from sklearn.semi_supervised import label_propagation

from sklearn.model_selection import cross_val_score

from sklearn.metrics import mean_squared_error
from sklearn.metrics import auc

from sklearn import preprocessing

import matplotlib.pyplot as plt

from collections import OrderedDict


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

from sklearn.model_selection import train_test_split


import random

from sklearn import metrics



#from pcapng import FileScanner

##from pycopkmeans import *
##from constrained_kmeans import *
#from cop_kmeans import *

def avg(lst): 
    return sum(lst) / len(lst)


def calculateFeaturesAccel(data): # put this parameter in classification
    
#    features = np.zeros((2722,33))
#
#    for i in range(0,2722):
#        
#        xData = (data[i][0:128])
#        yData = (data[i][128:256])
#        zData = (data[i][256:384])
        
    
    #print ("Here") 

    data = np.asfarray(data, dtype='float')    
    
    
    nData = len(data)
    nwindow = len(data[0])    
    
    features = np.zeros((nData,130))
 
 
    for i in range(0,nData):
         
        xData = (data[i][0:int(nwindow/3)])
        yData = (data[i][int(nwindow/3):int((nwindow/3)*2)])
        zData = (data[i][int((nwindow/3)*2):nwindow])
        
        
#        xData = signal.medfilt(xData, 127)
#        yData = signal.medfilt(yData, 127)
#        zData = signal.medfilt(zData, 127)
        #print (str(xData[0]) + ", " + str(newxData[0]))
        
        magData = np.sqrt(np.square(xData) + np.square(yData) + np.square(zData))
         
        ##########################################
        # Feature Subect 1: Time Domain Features #        
        ##########################################
         
        # Calculate the Min
        minX = np.min(xData)
        minY = np.min(yData)
        minZ = np.min(zData)
        features[i][0:3] = [minX, minY, minZ]
    
     
        # Max
        maxX = np.max(xData)
        maxY = np.max(yData)
        maxZ = np.max(zData)
        features[i][3:6] = [maxX, maxY, maxZ]
        #print features[nData-1][3:6]



     
        # Mean
        meanX = np.mean(xData)
        meanY = np.mean(yData)
        meanZ = np.mean(zData)
        features[i][6:9] = [meanX, meanY, meanZ]



         
        # Standard Deviation
        stdDevX = np.std(xData)
        stdDevY = np.std(yData)
        stdDevZ = np.std(zData)
        features[i][9:12] = [stdDevX, stdDevY, stdDevZ]  
        #print features[nData-1][9:12]

 
         
        # Pairwise Correlation
        corrXY = np.correlate(xData,yData)
        corrXZ = np.correlate(xData,zData)
        corrYZ = np.correlate(yData,zData)
        features[i][12:15] = [corrXY, corrXZ, corrYZ]  
        #print features[nData-1][12:15]
 
 
        # Zero Crossing-Rate
        zeroRateX = zero_cross_rate(xData)
        zeroRateY = zero_cross_rate(yData)
        zeroRateZ = zero_cross_rate(zData)
        features[i][15:18] = [zeroRateX, zeroRateY, zeroRateZ]


        #Skew
        xskew = stats.skew(xData)
        yskew = stats.skew(yData)
        zskew = stats.skew(zData)
        features[i][18:21] = [xskew, yskew, zskew]


         
        #Kurtosis
        xkurt = stats.kurtosis(xData)
        ykurt = stats.kurtosis(yData)
        zkurt = stats.kurtosis(zData)
        features[i][21:24] = [xkurt, ykurt, zkurt]


        #Signal-to-noise ratio
        xsnr = features[i][6]/features[i][9]
        ysnr = features[i][7]/features[i][10]
        zsnr = features[i][8]/features[i][11]
        features[i][24:27] = [xsnr, ysnr, zsnr]
        
         
        #Mean cross rate
        xMCR = mean_cross_rate(xData)
        yMCR = mean_cross_rate(yData)
        zMCR = mean_cross_rate(zData)
        features[i][27:30] = [xMCR, yMCR, zMCR]
        
         
        #Trapezoidal area
        xArea = np.trapz(xData)
        yArea = np.trapz(yData)
        zArea = np.trapz(zData)
        features[i][30:33] = [xArea, yArea, zArea]
        
        
        xFFT = np.fft.fft((xData))
        yFFT = np.fft.fft((yData))
        zFFT = np.fft.fft((zData))
        
        xSigEnergy = (1.0/128.0)*sum(np.power(np.absolute(xFFT),2))
        ySigEnergy = (1.0/128.0)*sum(np.power(np.absolute(yFFT),2))
        zSigEnergy = (1.0/128.0)*sum(np.power(np.absolute(zFFT),2))
        features[i][33:36] = [xSigEnergy, ySigEnergy, zSigEnergy]
        
        
        xFFTFreq = fftpack.fftfreq(xFFT.size, 1.0/50.0)
        yFFTFreq = fftpack.fftfreq(yFFT.size, 1.0/50.0)
        zFFTFreq = fftpack.fftfreq(zFFT.size, 1.0/50.0)
        #print features[nData-1][36:40] 
        
        xTopFreqs = getTopFreqs(xFFT, xFFTFreq)
        yTopFreqs = getTopFreqs(yFFT, yFFTFreq)
        zTopFreqs = getTopFreqs(zFFT, zFFTFreq)
        features[i][36:48] = np.concatenate([xTopFreqs,yTopFreqs,zTopFreqs])
    
        
        # FFT
        # Sampling Rate 50Hz
        # Nyquist Frequency 25Hz        
        features[i][48:72] = np.concatenate([np.absolute(xFFT[0:8]),np.absolute(yFFT[0:8]),np.absolute(zFFT[0:8])])
        
        #Interquartile
        xiqr = stats.iqr(xData)
        yiqr = stats.iqr(yData)
        ziqr = stats.iqr(zData)
        features[i][72:75] = [xiqr, yiqr, ziqr]
        
        #Percentile 25%
        xperc25 = stats.scoreatpercentile(xData, 25)
        yperc25 = stats.scoreatpercentile(yData, 25)
        zperc25 = stats.scoreatpercentile(zData, 25)
        features[i][75:78] = [xperc25, yperc25, zperc25]
        
        #Percentile 75%
        xperc75 = stats.scoreatpercentile(xData, 75)
        yperc75 = stats.scoreatpercentile(yData, 75)
        zperc75 = stats.scoreatpercentile(zData, 75)
        features[i][78:81] = [xperc75, yperc75, zperc75]
        
        #Root Mean Square
        xrms = np.sqrt(np.mean(xData**2))
        yrms = np.sqrt(np.mean(yData**2))
        zrms = np.sqrt(np.mean(zData**2))
        features[i][81:84] = [xrms, yrms, zrms]
        
        x2mom = stats.moment(xData, moment=2)
        y2mom = stats.moment(yData, moment=2)
        z2mom = stats.moment(zData, moment=2)
        features[i][84:87] = [x2mom, y2mom, z2mom]
        
        x3mom = stats.moment(xData, moment=3)
        y3mom = stats.moment(yData, moment=3)
        z3mom = stats.moment(zData, moment=3)
        features[i][87:90] = [x3mom, y3mom, z3mom]
        
        x4mom = stats.moment(xData, moment=4)
        y4mom = stats.moment(yData, moment=4)
        z4mom = stats.moment(zData, moment=4)
        features[i][90:93] = [x4mom, y4mom, z4mom]
        
        xkurt2 = np.true_divide(x4mom, np.square(x2mom))
        ykurt2 = np.true_divide(y4mom, np.square(y2mom))
        zkurt2 = np.true_divide(z4mom, np.square(z2mom))
        features[i][93:96] = [xkurt2, ykurt2, zkurt2]
        
#==============================================================================
#         xskew2 = np.nan_to_num(x3mom / (np.sqrt(x3mom) * np.power(x3mom, 3)))
#         yskew2 = np.nan_to_num(np.true_divide(y3mom, np.sqrt(y3mom) * np.float_power(y3mom, 3)))
#         zskew2 = np.nan_to_num(np.true_divide(z3mom, np.sqrt(z3mom) * np.float_power(z3mom, 3)))
#         features[i][96:99] = [xskew2, yskew2, zskew2]
#         
#         xentropy = np.nan_to_num(stats.entropy(xData))
#         yentropy = np.nan_to_num(stats.entropy(yData))
#         zentropy = np.nan_to_num(stats.entropy(zData))
#         features[i][99:102] = [xentropy, yentropy, zentropy]
#==============================================================================
        
        xenergy = np.sum(np.square(np.absolute(xData)))
        yenergy = np.sum(np.square(np.absolute(yData)))
        zenergy = np.sum(np.square(np.absolute(zData)))
        features[i][102:105] = [xenergy, yenergy, zenergy]
        
        #Mean of Absolute Value of First Difference
        xmean1d = (1.0/len(xData)) * np.sum(np.absolute(np.diff(xData, n=1)))
        ymean1d = (1.0/len(yData)) * np.sum(np.absolute(np.diff(yData, n=1)))
        zmean1d = (1.0/len(zData)) * np.sum(np.absolute(np.diff(zData, n=1)))
        features[i][105:108] = [xmean1d, ymean1d, zmean1d]
        
        #Mean of Absolute Value of Second Difference
        xmean2d = (1.0/len(xData)) * np.sum(np.absolute(np.diff(xData, n=2)))
        ymean2d = (1.0/len(yData)) * np.sum(np.absolute(np.diff(yData, n=2)))
        zmean2d = (1.0/len(zData)) * np.sum(np.absolute(np.diff(zData, n=2)))
        features[i][105:108] = [xmean2d, ymean2d, zmean2d]
        
        ##################################################################################
        
        ##########################################
        # Feature Subect 1: Time Domain Features #        
        ##########################################
         
        # Calculate the Min
        minMag = np.min(magData)
        features[i][108] = minMag
     
        # Max
        maxMag = np.max(magData)
        features[i][109] = maxMag
        #print features[nData-1][3:6]

        # Mean
        meanMag = np.mean(magData)
        features[i][110] = meanMag
         
        # Standard Deviation
        stdDevMag = np.std(magData)
        features[i][111] = stdDevMag 
 
        # Zero Crossing-Rate
        zeroRateMag = zero_cross_rate(magData)
        features[i][112] = zeroRateMag

        #Skew
        magskew = stats.skew(magData)
        features[i][113] = magskew
         
        #Kurtosis
        magkurt = stats.kurtosis(magData)
        features[i][114] = magkurt
     
        #Mean cross rate
        magMCR = mean_cross_rate(magData)
        features[i][115] = magMCR
        
         
        #Trapezoidal area
        magArea = np.trapz(magData)
        features[i][116] = magArea
        
        
        #Interquartile
        magiqr = stats.iqr(magData)
        features[i][117] = magiqr
        
        #Percentile 25%
        magperc25 = stats.scoreatpercentile(magData, 25)
        features[i][118] = magperc25
        
        #Percentile 75%
        magperc75 = stats.scoreatpercentile(magData, 75)
        features[i][119] = magperc75
        
        #Root Mean Square
        magrms = np.sqrt(np.mean(magData**2))
        features[i][120] = magrms
        
        mag2mom = stats.moment(magData, moment=2)
        features[i][121] = mag2mom
        
        mag3mom = stats.moment(magData, moment=3)
        features[i][122] = mag3mom
        
        mag4mom = stats.moment(magData, moment=4)
        features[i][123] = mag4mom
        
        magkurt2 = np.true_divide(mag4mom, np.square(mag2mom))
        features[i][124] = magkurt2
        
#==============================================================================
#         magskew2 = np.nan_to_num(mag3mom / (np.sqrt(mag3mom) * np.power(mag3mom, 3)))
#         features[i][125] = magskew2
#         
#         magentropy = np.nan_to_num(stats.entropy(magData))
#         features[i][126] = magentropy
#==============================================================================
        
        magenergy = np.sum(np.square(np.absolute(magData)))
        features[i][127] = magenergy
        
        #Mean of Absolute Value of First Difference
        magmean1d = (1.0/len(magData)) * np.sum(np.absolute(np.diff(magData, n=1)))
        features[i][128] = magmean1d
        
        #Mean of Absolute Value of Second Difference
        magmean2d = (1.0/len(magData)) * np.sum(np.absolute(np.diff(magData, n=2)))
        features[i][129] = magmean2d

     


    return features



def calculateFeaturesGyro(data): # put this parameter in classification
    
#    features = np.zeros((2722,33))
#
#    for i in range(0,2722):
#        
#        xData = (data[i][0:128])
#        yData = (data[i][128:256])
#        zData = (data[i][256:384])
        
    data = np.delete(data, len(data[0])-1, 1)
    data = np.delete(data, len(data[0])-1, 1)
    #print ("Here") 

    data = np.asfarray(data, dtype='float')    
    
    
    nData = len(data)
    nwindow = len(data[0])    
    
    features = np.zeros((nData,30))
 
 
    for i in range(0,nData):
         
        xData = (data[i][0:int(nwindow/3)])
        yData = (data[i][int(nwindow/3):int((nwindow/3)*2)])
        zData = (data[i][int((nwindow/3)*2):nwindow])
         
        ##########################################
        # Feature Subect 1: Time Domain Features #        
        ##########################################
         
        # Calculate the Min
        minX = np.min(xData)
        minY = np.min(yData)
        minZ = np.min(zData)
        features[i][0:3] = [minX, minY, minZ]



     
        # Max
        maxX = np.max(xData)
        maxY = np.max(yData)
        maxZ = np.max(zData)
        features[i][3:6] = [maxX, maxY, maxZ]
        #print features[nData-1][3:6]



     
        # Mean
        meanX = np.mean(xData)
        meanY = np.mean(yData)
        meanZ = np.mean(zData)
        features[i][6:9] = [meanX, meanY, meanZ]



         
        # Standard Deviation
        stdDevX = np.std(xData)
        stdDevY = np.std(yData)
        stdDevZ = np.std(zData)
        features[i][9:12] = [stdDevX, stdDevY, stdDevZ]  
        #print features[nData-1][9:12]

 
         
#==============================================================================
#         # Pairwise Correlation
#         corrXY = np.correlate(xData,yData)
#         corrXZ = np.correlate(xData,zData)
#         corrYZ = np.correlate(yData,zData)
#         features[i][12:15] = [corrXY, corrXZ, corrYZ]  
#         #print features[nData-1][12:15]
#==============================================================================
 
 
#==============================================================================
#         # Zero Crossing-Rate
#         zeroRateX = zero_cross_rate(xData)
#         zeroRateY = zero_cross_rate(yData)
#         zeroRateZ = zero_cross_rate(zData)
#         features[i][15:18] = [zeroRateX, zeroRateY, zeroRateZ]
#==============================================================================


        #Skew
        xskew = stats.skew(xData)
        yskew = stats.skew(yData)
        zskew = stats.skew(zData)
        features[i][12:15] = [xskew, yskew, zskew]


         
        #Kurtosis
        xkurt = stats.kurtosis(xData)
        ykurt = stats.kurtosis(yData)
        zkurt = stats.kurtosis(zData)
        features[i][15:18] = [xkurt, ykurt, zkurt]
        
        #Interquartile
        xiqr = stats.iqr(xData)
        yiqr = stats.iqr(yData)
        ziqr = stats.iqr(zData)
        features[i][18:21] = [xiqr, yiqr, ziqr]
        
        #Percentile 25%
        xperc25 = stats.scoreatpercentile(xData, 25)
        yperc25 = stats.scoreatpercentile(yData, 25)
        zperc25 = stats.scoreatpercentile(zData, 25)
        features[i][21:24] = [xperc25, yperc25, zperc25]
        
        #Percentile 75%
        xperc75 = stats.scoreatpercentile(xData, 75)
        yperc75 = stats.scoreatpercentile(yData, 75)
        zperc75 = stats.scoreatpercentile(zData, 75)
        features[i][24:27] = [xperc75, yperc75, zperc75]
        
        #Root Mean Square
        xrms = np.sqrt(np.mean(xData**2))
        yrms = np.sqrt(np.mean(yData**2))
        zrms = np.sqrt(np.mean(zData**2))
        features[i][27:30] = [xrms, yrms, zrms]


#==============================================================================
#         #Signal-to-noise ratio
#         xsnr = features[i][6]/features[i][9]
#         ysnr = features[i][7]/features[i][10]
#         zsnr = features[i][8]/features[i][11]
#         features[i][24:27] = [xsnr, ysnr, zsnr]
#         
#          
#         #Mean cross rate
#         xMCR = mean_cross_rate(xData)
#         yMCR = mean_cross_rate(yData)
#         zMCR = mean_cross_rate(zData)
#         features[i][27:30] = [xMCR, yMCR, zMCR]
#         
#          
#         #Trapezoidal area
#         xArea = np.trapz(xData)
#         yArea = np.trapz(yData)
#         zArea = np.trapz(zData)
#         features[i][30:33] = [xArea, yArea, zArea]
#         
#         
#         xFFT = np.fft.fft((xData))
#         yFFT = np.fft.fft((yData))
#         zFFT = np.fft.fft((zData))
#         
#         xSigEnergy = (1.0/128.0)*sum(np.power(np.absolute(xFFT),2))
#         ySigEnergy = (1.0/128.0)*sum(np.power(np.absolute(yFFT),2))
#         zSigEnergy = (1.0/128.0)*sum(np.power(np.absolute(zFFT),2))
#         features[i][33:36] = [xSigEnergy, ySigEnergy, zSigEnergy]
#         
#         
#         xFFTFreq = fftpack.fftfreq(xFFT.size, 1.0/50.0)
#         yFFTFreq = fftpack.fftfreq(yFFT.size, 1.0/50.0)
#         zFFTFreq = fftpack.fftfreq(zFFT.size, 1.0/50.0)
#         #print features[nData-1][36:40] 
#         
#         xTopFreqs = getTopFreqs(xFFT, xFFTFreq)
#         yTopFreqs = getTopFreqs(yFFT, yFFTFreq)
#         zTopFreqs = getTopFreqs(zFFT, zFFTFreq)
#         features[i][36:48] = np.concatenate([xTopFreqs,yTopFreqs,zTopFreqs])
#     
#         
#         # FFT
#         # Sampling Rate 50Hz
#         # Nyquist Frequency 25Hz        
#         features[i][48:72] = np.concatenate([np.absolute(xFFT[0:8]),np.absolute(yFFT[0:8]),np.absolute(zFFT[0:8])])
# 
#==============================================================================
     


    return features


def calculateFeaturesHR(data): # put this parameter in classification
            
    data = np.delete(data, len(data[0])-1, 1)
    data = np.delete(data, len(data[0])-1, 1)

    data = np.asfarray(data, dtype='float')    
        
    nData = len(data)
    
    features = np.zeros((nData,1))
 
 
    for i in range(0,nData):
        # Mean
        meanHR = np.mean(data[i])
        features[i][0] = meanHR
    return features

def zero_cross_rate(X):
    count = 1
    cross = 0
    while count < len(X):
        if X[count-1] > 0 and X[count] < 0:
            cross+=1
            count+=1
        elif X[count-1] < 0 and X[count]> 0:
            cross+=1
            count+=1
        else:
            count+=1
         
    return cross
 
 
 
def mean_cross_rate(X):
  mean = np.mean(X)
  count = 1
  cross = 0
  while count < len(X):
        if X[count-1] > mean and X[count] < mean:
            cross+=1
            count+=1
        elif X[count-1] < mean and X[count]> mean:
            cross+=1
            count+=1
        else:
            count+=1
         
  return cross



def getTopFreqs(fftData,fftFreq):
    ind = np.argpartition(np.abs(fftData)**2, -4)[-4:]
    freqs = fftFreq[ind[np.argsort(fftData[ind])]]
        
    return freqs

##########################################################################################################################

def plot_confusion_matrix(cm, classes,
                          normalize=False,
                          title='Confusion matrix',
                          cmap=plt.cm.Blues):
    """
    This function prints and plots the confusion matrix.
    Normalization can be applied by setting `normalize=True`.
    """
    
    plt.figure(figsize=(8,8))
    if normalize:
        cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
        print("Normalized confusion matrix")
    else:
        print('Confusion matrix, without normalization')

    print(cm)

    plt.imshow(cm, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(len(classes))
    plt.xticks(tick_marks, classes, rotation=90)
    plt.yticks(tick_marks, classes)

    fmt = '.2f' if normalize else 'd'
    thresh = cm.max() / 2.
    for i, j in itertools.product(range(cm.shape[0]), range(cm.shape[1])):
        plt.text(j, i, format(cm[i, j], fmt),
                 horizontalalignment="center",
                 color="white" if cm[i, j] > thresh else "black")

    plt.ylabel('True label')
    plt.xlabel('Predicted label')
    #plt.tight_layout(pad=.5)
    plt.savefig("plot.png", format='png', dpi=300)
    plt.show()
    
    print ("\n")



def median_filter(data, f_size):
	lgth, num_signal=data.shape
	f_data=np.zeros([lgth, num_signal])
	for i in range(num_signal):
		f_data[:,i]=signal.medfilt(data[:,i], f_size)
	return f_data



########################################## Window Extractor! ###############################################################    
## Parameters: Range starts at 0
col_time = 0
col_x = 1
col_y = 2
col_z = 3
col_x_gyro = 4
col_y_gyro = 5
col_z_gyro = 6

col_HR = 8
col_user = 9
col_class = 12
win_start = 1


if platform == 'win32':
    pathSep = "\\"
    pathSepD = "\\"
else:
    pathSep = "/"
    pathSepD = "/"
    
input_file_app = "packet_user.csv"

#user_start = 100104
#user_end = 100111

## Look into variable these
window_size = 96
shift_amount = 32

## 0 if just set; 1 if whole range
window_study = 0

if (window_study == 0):
    a1 = window_size
    a2 = shift_amount
else:
    a1 = 1
    a2 = 1
    
input_file = "rawData128SinglePoint.csv"

if not os.path.exists("output"):
        os.makedirs("output")

#for user in range(user_start, user_end + 1):
        
#min_window = 32
min_window = 96
max_window = 96 #int(7 * 64)
increment = 32
sample_rate = 62
  
twin_start = win_start

confMat = []
allanalysis = []
                           
result=np.array(list(csv.reader((open(input_file, "rbU")), delimiter=",")))





print ("Beginning Feature Extraction! This may take a few minutes!") 


labels = result[:,len(result[0])-1]
users = result[:,len(result[0])-2]

result = np.delete(result, len(result[0])-1, 1)
result = np.delete(result, len(result[0])-1, 1)

out = calculateFeaturesAccel(result)

le = preprocessing.LabelEncoder()
le.fit(labels)

#labels = le.transform(labels) 

out = np.column_stack((out,labels))
out = np.column_stack((out,users))

X = out


print ("Features extracted!")

################ Remove Other Users ##################


for a in range(len(X)): #LABEL ACTIVITIES
    if (0 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Fist Pump"
    elif (1 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "High Wave"
    elif (2 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Hand Shake"
    elif (3 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Fist Bump"
    elif (4 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Low Wave"
    elif (5 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Point"

    elif (9 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Motion Over"
    elif (10 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "High Five"
    elif (11 == int(X[a][len(X[0])-2])):
        X[a][len(X[0])-2] = "Clap"
    
rawX = X


#%%

for xy in range(32):
        
    X = rawX    
    
    indexes = []
    indexesQ = []
    
    XQ = X
    
    
    
    for a in range(len(X)): #REMOVE OTHER USERS
        if (xy == int(X[a][len(X[0])-1])):
            indexes.append(a)
            
        else:
            indexesQ.append(a)
            
    X = np.delete(X, indexes, axis=0)
    XQ = np.delete(XQ, indexesQ, axis=0)
        
        
        
    for z in range(3):
        np.random.shuffle(X)
        np.random.shuffle(XQ)
   
    
    true_labels = X[:,len(X[0])-2]
    true_user = X[:,len(X[0])-1]
    X = np.delete(X, len(X[0])-1, 1)
    X = np.delete(X, len(X[0])-1, 1)
    
    #X = np.delete(X, 0, 1)
    #X = np.delete(X, 0, 1)
    X = X.astype(np.float)
    
    
    X = preprocessing.scale(X)
        
    
    ##########################################
    
    true_labelsQ = XQ[:,len(XQ[0])-2]
    true_userQ = XQ[:,len(XQ[0])-1]
    XQ = np.delete(XQ, len(XQ[0])-1, 1)
    XQ = np.delete(XQ, len(XQ[0])-1, 1)
    
    #X = np.delete(X, 0, 1)
    #X = np.delete(X, 0, 1)
    XQ = XQ.astype(np.float)
    
    
    XQ = preprocessing.scale(XQ)
        
    
    
    
    
    # Cross- Validation Schemes
    #kf = KFold(5, shuffle=False)
       
    # Classifier Selection
    #clf = LDA()
    clf = LogisticRegression()
    #clf = KNeighborsClassifier(n_neighbors=3)
    #clf = svm.SVC(kernel="linear")
    #clf = svm.SVC(kernel="rbf")
    #clf = tree.DecisionTreeClassifier()
    #clf = GaussianNB()
    
    #clf = RandomForestClassifier(n_estimators=160, max_features="sqrt")
    
    
    #for a in range (len(true_user)):
    #    if (0 == int(true_user[a])):
    #        true_user[a] = "Auth"
    #    else:
    #        true_user[a] = "Unauth"
    #
    
    
    X_train, X_test, Y_train, Y_test = train_test_split(X, true_labels, test_size=0.0)
    #X_test, X_val, Y_test, Y_val = train_test_split(X_test, Y_test, test_size=0.5)
    
    X_test = XQ
    Y_test = true_labelsQ
    
    
    allPredictions = []
    allClassifications = []
    
    #print(cluster_labels)
    #cluster_labels = np.array(cluster_labels)
    #print(cluster_labels)
    

    clf.fit(X_train, Y_train)
    predictions = clf.predict(X_test)
    allPredictions.append( predictions)
    allClassifications.append(Y_test)
        
    #print(avg(mse))
        
    
    allPredictions = np.concatenate(allPredictions)
    allClassifications = np.concatenate(allClassifications) 
    
    labels = ["Clap", "Fist Bump", "Fist Pump", "Hand Shake", "High Five", "High Wave", "Low Wave", "Motion Over", "Point"]
    print(classification_report(allClassifications,allPredictions, labels=labels))
    
    
    print("F1 score weighted: " + str(metrics.f1_score(allClassifications,allPredictions, average="macro")))
    
    cnf_matrix = confusion_matrix(allClassifications,allPredictions, labels=labels)
    
    cm = cnf_matrix
    cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
    confMat.append(cm)


#%%
print (len(confMat))
print (len(confMat[0]))
print (len(confMat[0][0]))

buckets = [[0 for col in range(9)] for row in range(9)]

for x in range (len(confMat)):
    for y in range(len(confMat[x])):
        for z in range (len(confMat[x][y])):
            buckets[y][z] = buckets[y][z] + confMat[x][y][z]
            
for x in range(len(buckets)):
    for y in range (len(buckets[x])):
        buckets[x][y] = buckets[x][y] / 32
        
#print (buckets)
cnf_matrix = np.asarray(buckets)
            
#avgCM = cm
plot_confusion_matrix(cnf_matrix, classes=labels, normalize=True, title='Normalized confusion matrix')



#analysiswindow.append(metrics.f1_score(allClassifications,allPredictions, average="weighted"))

## Range of `n_estimators` values to explore.
#min_estimators = 15
#max_estimators = 200
#
#
## NOTE: Setting the `warm_start` construction parameter to `True` disables
## support for parallelized ensembles but is necessary for tracking the OOB
## error trajectory during training.
#ensemble_clfs = [
#    ("RandomForestClassifier, max_features='sqrt'",
#        RandomForestClassifier(n_estimators=100,
#                               warm_start=True, oob_score=True,
#                               max_features="sqrt")), #random_state=RANDOM_STATE
#    ("RandomForestClassifier, max_features='log2'",
#        RandomForestClassifier(n_estimators=100,
#                               warm_start=True, max_features='log2',
#                               oob_score=True)),
#    ("RandomForestClassifier, max_features=None",
#        RandomForestClassifier(n_estimators=100,
#                               warm_start=True, max_features=None,
#                               oob_score=True))
#]
#
#
#error_rate = OrderedDict((label, []) for label, _ in ensemble_clfs)
#error_rate_avg = OrderedDict((label, []) for label, _ in ensemble_clfs)
#error_rate_max = OrderedDict((label, []) for label, _ in ensemble_clfs)
#error_rate_min = OrderedDict((label, []) for label, _ in ensemble_clfs)
#
#for label, clf in ensemble_clfs: 
#    for i in range(min_estimators, max_estimators + 1, 5):
#        #print (label + " n = " +  str(i))
#        clf.set_params(n_estimators=i)
#        clf.fit(X_train, Y_train)
#        
#        # Record the OOB error for each `n_estimators=i` setting.
#        oob_error = 1 - clf.oob_score_
#        
#        error_rate[label].append((i, oob_error))
#        #error_rate_avg[label].append((i, avg(error_rate)))
#        #error_rate_max[label].append((i, max(error_rate)))
#        #error_rate_min[label].append((i, min(error_rate)))
#            
##%%
#
#plt.clf()
## Generate the "OOB error rate" vs. "n_estimators" plot.
#for label, clf_err in error_rate.items():    
#    xs, ys = zip(*clf_err)
#    plt.plot(xs, ys, label=(label))
#    
#plt.xlim(min_estimators, max_estimators)
#plt.xlabel("n_estimators")
#plt.ylabel("OOB error rate")
#plt.legend(loc="upper right")
#plt.show()


#print (analysiswindow)
#allanalysis.append(analysiswindow)

#print(allanalysis)