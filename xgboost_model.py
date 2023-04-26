import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split,GridSearchCV
from sklearn.metrics import accuracy_score
from sklearn.preprocessing import LabelEncoder
from imblearn.over_sampling import SMOTE
from xgboost import XGBClassifier
import matplotlib.pyplot as plt
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
import os
import pickle
print("modules loaded")
df = pd.read_csv("Dataset\healthcare-dataset-stroke-data.csv")
dataset = pd.read_csv("Dataset\healthcare-dataset-stroke-data.csv", usecols=['gender','age','hypertension','heart_disease','avg_glucose_level','bmi','smoking_status','stroke'])
dataset.fillna(0, inplace = True)#remove missing values
le1 = LabelEncoder()
le2 = LabelEncoder()
dataset['gender'] = pd.Series(le1.fit_transform(dataset['gender'].astype(str)))
dataset['smoking_status'] = pd.Series(le1.fit_transform(dataset['smoking_status'].astype(str)))

X = dataset.drop(['stroke'], axis=1).to_numpy()
y = dataset['stroke'].to_numpy()
indices = np.arange(X.shape[0])

indices = np.arange(X.shape[0])
np.random.shuffle(indices)#shuffle the dataset
X = X[indices]
Y = y[indices]

sm = SMOTE(random_state = 2) #defining smote object
X, y = sm.fit_resample(X, Y)
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=1)

#function to calculate all metrics
def calculateMetrics(algorithm, testY, predict):
    labels = {"No Brain Stroke Detected", "Brain Stroke Detected"}
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

dir_path="XGBoostmodel.pkl"
if os.path.exists(dir_path) == False:

  params = {'max_depth':[5,6,7,8], 'learning_rate':[0.1], 'n_estimators':[x for x in range(70,100,5)]}

  xg=XGBClassifier(objective='binary:logistic',reg_lambda=0.1, reg_alpha=0.1)
  grid_search = GridSearchCV(xg, param_grid=params, cv=7, scoring='accuracy')
  grid_search.fit(X_train, y_train)
  print("Best hyperparameters:", grid_search.best_params_)
  print("Best score:", grid_search.best_score_)

  model = grid_search.best_estimator_
  with open(dir_path,"wb") as f:
    pickle.dump(model,f)

else:
  print("Found model in directory")
  model=pickle.load(open(dir_path,"rb"))

pred = model.predict(X_test)
accuracy = accuracy_score(y_test, pred.round())
print("Accuracy: %.2f%%" % (accuracy * 100.0))
calculateMetrics("XGBoost",y_test,pred)

