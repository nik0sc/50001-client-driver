import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
from google.cloud.firestore_v1beta1._helpers import GeoPoint
import time

cred = credentials.Certificate("C:/Users/Lucus/Desktop/testsimul-11005-firebase-adminsdk-s6uon-3f9ec1cfed.json")
firebase_admin.initialize_app(cred)

db = firestore.client()

users_ref = db.collection(u'cars')
docs = users_ref.get()
    
#pre-resetting location
doc_ref = db.collection(u'ambulances').document(u'amb01') #at sutd
doc_ref.set({u'location': GeoPoint(1.339869,103.963183)})
doc_ref = db.collection(u'cars').document(u'car01') #at sutd entrance b
doc_ref.set({u'location': GeoPoint(1.340022,103.965254)})
doc_ref = db.collection(u'cars').document(u'car02')
doc_ref.set({u'location': GeoPoint(1.338974,103.963968)})
doc_ref = db.collection(u'cars').document(u'car03')
doc_ref.set({u'location': GeoPoint(1.341716,103.957552)})
doc_ref = db.collection(u'cars').document(u'car04')
doc_ref.set({u'location': GeoPoint(1.343823,103.954149)})
doc_ref = db.collection(u'cars').document(u'car05')
doc_ref.set({u'location': GeoPoint(1.339869,103.963183)})

time.sleep(10)
    
#cycle 1
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.3396963,103.96316980000006)})
doc_ref = db.collection(u'cars').document(u'car01') 
doc_ref.set({u'location': GeoPoint(1.339806,103.965129)})
time.sleep(5)

#cycle 2
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339693,103.964237)})
doc_ref = db.collection(u'cars').document(u'car01') 
doc_ref.set({u'location': GeoPoint(1.33965,103.965031)})
time.sleep(5)

#cycle 3
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339316,103.964861)})
doc_ref = db.collection(u'cars').document(u'car01') 
doc_ref.set({u'location': GeoPoint(1.339402,103.964871)})#car  now near ambulance
time.sleep(5)

#cycle 3a
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339527,103.964225)})
doc_ref = db.collection(u'cars').document(u'car01') 
doc_ref.set({u'location': GeoPoint(1.339236,103.964814)})
doc_ref = db.collection(u'cars').document(u'car02')
doc_ref.set({u'location': GeoPoint(1.339507,103.964261)})
time.sleep(5)

#cycle 4
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339527,103.962684)})
doc_ref = db.collection(u'cars').document(u'car01') 
doc_ref.set({u'location': GeoPoint(1.339132,103.964699)})
doc_ref = db.collection(u'cars').document(u'car02')
doc_ref.set({u'location': GeoPoint(1.339559,103.961867)})
time.sleep(5)

#cycle 5
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339582,103.962079)})
doc_ref = db.collection(u'cars').document(u'car01') 
doc_ref.set({u'location': GeoPoint(1.338914,103.96456)})
doc_ref = db.collection(u'cars').document(u'car02')
doc_ref.set({u'location': GeoPoint(1.339559,103.961867)})
time.sleep(5)

#cycle 6
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339721,103.961135)})
doc_ref = db.collection(u'cars').document(u'car02')
doc_ref.set({u'location': GeoPoint(1.339721,103.961135)})
time.sleep(5)

#cycle 7
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.340811,103.959165)})
doc_ref = db.collection(u'cars').document(u'car02')
doc_ref.set({u'location': GeoPoint(1.339721,103.961135)})
time.sleep(5)

#cycle 8
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.342096,103.958227)})
doc_ref = db.collection(u'cars').document(u'car03')
doc_ref.set({u'location': GeoPoint(1.34214,103.958061)})
time.sleep(5)

#cycle 9
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.343266,103.957665)})
doc_ref = db.collection(u'cars').document(u'car03')
doc_ref.set({u'location': GeoPoint(1.343522,103.957597)})
time.sleep(5)

#cycle 10
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.344338,103.957629)})
doc_ref = db.collection(u'cars').document(u'car03')
doc_ref.set({u'location': GeoPoint(1.344338,103.957629)})
time.sleep(5)

#cycle 11
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.344418,103.956298)})
doc_ref = db.collection(u'cars').document(u'car03')
doc_ref.set({u'location': GeoPoint(1.345332,103.957773)})
time.sleep(5)

#cycle 12
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.343384,103.955002)})
doc_ref = db.collection(u'cars').document(u'car04')
doc_ref.set({u'location': GeoPoint(1.343209,103.954667)})
time.sleep(5)

#cycle 13
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.342058,103.953337)})
doc_ref = db.collection(u'cars').document(u'car04')
doc_ref.set({u'location': GeoPoint(1.341962,103.953129)})
time.sleep(5)

#cycle 14
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.340703,103.95186)})
doc_ref = db.collection(u'cars').document(u'car04')
doc_ref.set({u'location': GeoPoint(1.341962,103.953129)})
time.sleep(5)

#cycle 15
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.340294,103.950808)})
doc_ref = db.collection(u'cars').document(u'car04')
doc_ref.set({u'location': GeoPoint(1.343083,103.952162)})
time.sleep(5)

#cycle 16
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.339316,103.964861)})
time.sleep(5)

#cycle 17
doc_ref = db.collection(u'ambulances').document(u'amb01')
doc_ref.set({u'location': GeoPoint(1.3738413000000003,103.7656732)})
time.sleep(5)




