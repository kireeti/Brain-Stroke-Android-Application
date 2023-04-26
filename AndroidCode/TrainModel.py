import pandas as pd
import numpy as np
from sklearn.metrics import accuracy_score
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import StandardScaler
from imblearn.over_sampling import SMOTE
from keras.utils.np_utils import to_categorical
import os
from keras.callbacks import ModelCheckpoint

from keras.layers import  MaxPooling2D
from keras.layers import Dense, Dropout, Activation, Flatten
from keras.layers import Convolution2D
from keras.models import Sequential, Model, load_model

from sklearn.metrics import precision_score
from sklearn.metrics import recall_score
from sklearn.metrics import f1_score
import seaborn as sns
from sklearn.metrics import roc_curve
from sklearn.metrics import roc_auc_score
from sklearn import metrics
import timeit
import matplotlib.pyplot as plt
from sklearn.metrics import confusion_matrix

'''
formerly smoked = 1
never smoked = 2
smokes = 3
Unknown = 0
male = 1
female = 0
'''

dataset = pd.read_csv("Dataset/healthcare-dataset-stroke-data.csv", usecols=['gender','age','hypertension','heart_disease','avg_glucose_level',
                                                                             'bmi','smoking_status','stroke'])
dataset.fillna(0, inplace = True)#remove missing values

le1 = LabelEncoder()
le2 = LabelEncoder()

dataset['gender'] = pd.Series(le1.fit_transform(dataset['gender'].astype(str)))
dataset['smoking_status'] = pd.Series(le1.fit_transform(dataset['smoking_status'].astype(str)))

dataset = dataset.values
X = dataset[:,0:dataset.shape[1]-1]
Y = dataset[:,dataset.shape[1]-1]

indices = np.arange(X.shape[0])
np.random.shuffle(indices)#shuffle the dataset
X = X[indices]
Y = Y[indices]

sm = SMOTE(random_state = 2) #defining smote object
X, Y = sm.fit_resample(X, Y)

X = np.reshape(X, (X.shape[0], X.shape[1], 1, 1))
print(X[0].tolist())
Y = to_categorical(Y)
X_train, X_test, y_train, y_test = train_test_split(X, Y, test_size=0.2, random_state = 42)
print(X.shape)

#function to calculate all metrics
def calculateMetrics(algorithm, testY, predict):
    labels = {"No Brain Stroke Detected", "Brain Stroke Detected"};
    p = precision_score(testY, predict,average='macro') * 100
    r = recall_score(testY, predict,average='macro') * 100
    f = f1_score(testY, predict,average='macro') * 100
    a = accuracy_score(testY,predict)*100
    print(algorithm+" Accuracy  : "+str(a))
    print(algorithm+" Precision : "+str(p))
    print(algorithm+" Recall    : "+str(r))
    print(algorithm+" FSCORE    : "+str(f))
    conf_matrix = confusion_matrix(testY, predict)
    fig, axs = plt.subplots(1,2,figsize=(12, 6))
    ax = sns.heatmap(conf_matrix, xticklabels = labels, yticklabels = labels, annot = True, cmap="viridis" ,fmt ="g", ax=axs[0]);
    ax.set_ylim([0,len(labels)])
    axs[0].set_title(algorithm+" Confusion matrix") 

    random_probs = [0 for i in range(len(testY))]
    p_fpr, p_tpr, _ = roc_curve(testY, random_probs, pos_label=1)
    plt.plot(p_fpr, p_tpr, linestyle='--', color='orange',label="True classes")
    ns_fpr, ns_tpr, _ = roc_curve(testY, predict, pos_label=1)
    axs[1].plot(ns_fpr, ns_tpr, linestyle='--', label='Predicted Classes')
    axs[1].set_title(algorithm+" ROC AUC Curve")
    axs[1].set_xlabel('False Positive Rate')
    axs[1].set_ylabel('True Positive rate')
    plt.show()
    
    
cnn_model = Sequential()
cnn_model.add(Convolution2D(32, (1, 1), input_shape = (X_train.shape[1], X_train.shape[2], X_train.shape[3]), activation = 'relu'))
cnn_model.add(MaxPooling2D(pool_size = (1, 1)))
cnn_model.add(Convolution2D(32, (1, 1), activation = 'relu'))
cnn_model.add(MaxPooling2D(pool_size = (1, 1)))
cnn_model.add(Flatten())
cnn_model.add(Dense(units = 256, activation = 'relu'))
cnn_model.add(Dense(units = y_train.shape[1], activation = 'softmax'))
cnn_model.compile(optimizer = 'adam', loss = 'categorical_crossentropy', metrics = ['accuracy'])
if os.path.exists("model/model_weights.hdf5") == False:
    model_check_point = ModelCheckpoint(filepath='model/model_weights.hdf5', verbose = 1, save_best_only = True)
    hist = cnn_model.fit(X_train, y_train, batch_size = 16, epochs = 150, validation_data=(X_test, y_test), callbacks=[model_check_point], verbose=1)    
else:
    cnn_model.load_weights("model/model_weights.hdf5")

predict = cnn_model.predict(X_test)
predict = np.argmax(predict, axis=1)
y_test = np.argmax(y_test, axis=1)
calculateMetrics("CNN Deep Learning Algorithm", y_test, predict)



