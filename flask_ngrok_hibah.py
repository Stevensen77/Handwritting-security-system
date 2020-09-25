# -*- coding: utf-8 -*-
"""
Created on Sun Jun 14 20:19:35 2020

@author: Steven
"""

from flask import Flask
from flask import request
from flask_ngrok import run_with_ngrok
from sklearn.preprocessing import MinMaxScaler,StandardScaler
from pyngrok import ngrok

app = Flask(__name__)
#run_with_ngrok(app)  # Start ngrok when app is run



# Open a HTTP tunnel on the default port 80
#ngrok.connect()
run_with_ngrok(app)  # Start ngrok when app is run

#ngrok.connect("C:\D\Semester 8\HIBAH DIKTI\flask_ngrok_hibah.py")

@app.route('/', methods=['POST','GET'])
def hello_world():
    if request.method == 'POST':

        import numpy as np
        from neupy import algorithms


        #TAHAP PRE-PROCESSING DATA
        
        
        fiturs_dict=  request.get_json()


        list_train = []
        list_testing = []
        list_nama=[]
        list_id_ktp=[]
        list_no_gambar=[]
        
        i=0
        # PEMBAGIAN DATA TRAIN DAN TEST
        
        for fitur in fiturs_dict:
            i=i+1
           # print("\n\nnilai i = ",i)
            a=len(fiturs_dict)
            # a = Panjang data train + test :
            if(i==a):
                list_testing.append(
                         [
                              fitur['NILAI_GREYSCALE'],
                              fitur['NILAI_INVARIANCE'],
                              fitur['NILAI_ENTROPY'],
                              fitur['NILAI_SKEWNESS'],
                              fitur['RELATIVE_SMOOTHNESS'],
                              fitur['NILAI_ENERGY'],
                              fitur['NILAI_CONTRAST']
             
            		     ]
                         )
              
              
            else:
                list_train.append(
                             [
                              fitur['NILAI_GREYSCALE'],
                              fitur['NILAI_INVARIANCE'],
                              fitur['NILAI_ENTROPY'],
                              fitur['NILAI_SKEWNESS'],
                              fitur['RELATIVE_SMOOTHNESS'],
                              fitur['NILAI_ENERGY'],
                              fitur['NILAI_CONTRAST']
                              ]
                )
                list_nama.append(
                         [
                            fitur['NAMA']
            		     ]
                         )
                list_id_ktp.append(
                         [
                            fitur['KTP']
            		     ]
                         )
                list_no_gambar.append(
                         [
                            fitur['NO_GAMBAR']
            		     ]
                         )
       
        print("\n\nISI data list TRAIN (20 awal) \n",list_train[:20]) 
        print("\n\nISI data list TESTING \n",list_testing)
        print("\n\nISI data list id ktp \n",list_id_ktp)
        print("\n\nISI data list no gambar : \n",list_no_gambar)
        
        (unique, counts) = np.unique(list_id_ktp, return_counts=True)
        (unique) = np.unique(list_id_ktp, return_counts=False)
        freq_id_ktp_unik = np.asarray((unique, counts)).T
        arr_id_ktp_unik = np.asarray((unique)).T
        print("\nISI Unique + count id_ktp",freq_id_ktp_unik)
        print("\nISI Unique id_ktp",arr_id_ktp_unik)
        
        print("Banyaknya ID KTP unik",len(freq_id_ktp_unik))
        
        (unique, counts) = np.unique(list_nama, return_counts=True)
        (unique) = np.unique(list_nama, return_counts=False)
        freq_nama= np.asarray((unique, counts)).T
        nama_unik = np.asarray((unique)).T
        print("\nISI Unique + count NAMA = \n",freq_nama)
        print("\nISI Unique NAMA \n",nama_unik)
        
        print("\nBanyaknya NAMA \n",len(freq_nama))
        

        
        array_train=np.array(list_train)
        print("\n\nIsi array train (20 awal) : ", array_train[:20])
        array_id_ktp=np.array(list_id_ktp)
        
        list_id_ktp_unik = arr_id_ktp_unik.tolist()
        list_id_ktp = array_id_ktp.tolist()
        print(f'\n\nList \n: {list_id_ktp_unik}')
        print(f'\n\nList id ktp \n: {list_id_ktp}')
        
        
        i=0
        persamaan_kelas_awal=[]
        
        
        
        for a in arr_id_ktp_unik:
            str_id_ktp_unik = ''.join(a)
            print("\nPrint str id unik  ",str_id_ktp_unik)
            persamaan_kelas_awal.append(str(i))
            #print("\n\ninisialisasi normalisasi : \n", persamaan_kelas_awal)
            i=i+1
        
        
        
        
        a= list_id_ktp
        list_label_id_ktp=[]
        for n, i in enumerate(a):
            str1=''.join(a[n])
            list_label_id_ktp.append(str1)
        print("LIST STRING ID KTP SEMUA : \n",list_label_id_ktp)
        
     
        list_str_no_gambar=[]
        for n, i in enumerate(list_no_gambar):
            str1=''.join(list_no_gambar[n])
            list_str_no_gambar.append(str1)
        print("LIST STRING NO GAMBAR : \n",list_str_no_gambar)
        
        results = list(map(int,list_label_id_ktp))
        print("\n\nlist int ID ktp :",results)
        
        results2 = list(map(str,list_nama))
        print("\n\nlist str nama:",results2)
        
  
        list_label_nama_pemilik=[]
        
        
        for n, i in enumerate(results):
            list_label_id_ktp[n] = i-1
            list_label_nama_pemilik.append(list_nama[n])
            
        print("\n\nLIST Pelabelan NAMA : ",list_label_nama_pemilik)
        print("\n\nLIST STRING kelas di jadikan urut mulai dari nol : \n",list_label_id_ktp)
        
        
        
        list_int_target = list(map(int,list_label_id_ktp))
        
        array_target=np.array(list_int_target)
        
        list_int_testing=[]
        for n, i in enumerate(list_testing):
            list_int= list(map(int,i))
            list_int_testing.append(list_int)
        
        print("\n\nLIST INT testing: \n",list_int_testing)

        array_testing=np.array(list_testing)
        
        scaler = MinMaxScaler(feature_range = (0, 1))
        
        array_minmax_testing = np.array(array_testing)
        arr_minmax_testing = scaler.fit_transform(array_minmax_testing)
        print("\n\n Minmaxscaler Testing isi : \n",arr_minmax_testing)
                
        array_minmax_train = np.vstack(list_train)
        arr_minmax_train = scaler.fit_transform(array_minmax_train)
        print("\nn\ Minmaxscaler Train isi (20 awal) : \n",arr_minmax_train[:20])


        list_testing_minmax=arr_minmax_testing.tolist()
        
        print("\n\nIsi array train : \n", array_train[:20])
        print("\n\nIsi list test : \n", list_testing_minmax)
    
        print("\n\nISI data list TESTING \n",list_testing)
        
        print("\n\nIsi array test : \n",  array_testing)
        print("\nIsi array target : \n", array_target)
        

        #TAHAP PROCESSING LVQ
        
        lvqnet = algorithms.LVQ3(n_inputs=len(fiturs_dict), n_classes=len(list_id_ktp_unik),
                                 verbose=True,step=0.005,epsilon=0.05)
        lvqnet.train(array_train, array_target, epochs=100)
        output_kelas= lvqnet.predict(list_testing)
        
        
        for i,a in enumerate(list_label_id_ktp):
            if(output_kelas==list_label_id_ktp[i]):
                hasil_nama=list_label_nama_pemilik[i]

        
        
        
        str_nama = ''.join(hasil_nama)
        str_output_kelas = np.array_str(output_kelas) 
        str_output_kelas = str_output_kelas.replace("[", "").replace("]", "")
        print("\n\nMasuk ke kelas : ",str_output_kelas)
        kelas_hasil = str_output_kelas,str_nama
        
        print(kelas_hasil)
        
        print("===========================================================================================")


        return str(kelas_hasil)

    else:
        return "bukan POST"




    

if __name__ == '__main__':
    app.run()