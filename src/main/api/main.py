from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import json
import time
from dateutil import parser

from fuzzywuzzy import fuzz,process
from ultralytics import YOLO

import cv2

import numpy as np

import torch

import os

import shutil

from paddleocr import PaddleOCR

import concurrent.futures

import threading


directoryPath = "./file_with_segmentations"

json_file = "new_json.json"

ocr_model = PaddleOCR(det_db_score_mode='slow',lang='fr', use_angle_cls=True, show_log=False)

model = YOLO('best_ever.pt')
model_champs_NAME_DATE_DOS = YOLO('best_train6.pt')
model_champs_PPA_FORM = YOLO('best_train4.pt')

vignette_color ={'0':'Green' , '1':'White' , '2': 'Red'}
classes_names={'0':'Forme' , '1':'Dossage' , '2': 'PPA' , '3' : 'Date' , '4': 'Name'}
input_size = (640, 640)


#Getting_date.py part
#################################
def symbol(part, sym):
    ddf = part.split(sym)[1]
    if len(ddf) > 2 and ddf[2] == '1':
        ddf = ddf[:2] + '/' + ddf[3:]
        return ddf
    if '-' in ddf:
        ddf = ddf.replace("-", "/")
        return ddf

    else:
        for month, value in month_mapping.items():
            if month in ddf:
                ddf = ddf.replace(month, value)
        return ddf


def gen(d, s):
    if ':' in d:
        resultat = symbol(d, ':')
        return resultat

    elif '.' in d:
        resultat = symbol(d, '.')
        return resultat
    else:
        resultat = symbol(d, s)
        return resultat


month_mapping = {
    'jan': '01/', 'feb': '02/', 'mar': '03/', 'apr': '04/', 'mai': '05/', 'jun': '06/',
    'jul': '07/', 'aug': '08/', 'sep': '09/', 'oct': '10/', 'nov': '11/', 'dec': '12/'
}


def getting_date(d):
    if len(d) > 0:
        d = d.lower()
        d = d.replace(" ", "")

        if ('ot' in d) or ('0t' in d):
            if ':' in d:
                final = d.split(':')[1]
                return ('lot', final)

            elif '.' in d:
                final = d.split('.')[1]

                return ('lot', final)
            else:
                final = d.split('t')[1]
                return ('lot', final)

        if 'df' in d:
            final = gen(d, 'f')
            return ('DDF', final)

        if 'dp' in d:
            final = gen(d, 'p')
            return ('DDP', final)

        if 'ab' in d:
            final = gen(d, 'b')
            return ('DDF', final)

        if 'er' in d:
            final = gen(d, 'r')
            return ('DDP', final)

        if 'xp' in d:
            final = gen(d, 'p')
            return ('DDP', final)


# HOW TO USE IT
# date is the OCR output , predict te3 walid f Notebook
# returns a key and value , Key can be 'Lot' for LOT , DDP for peremption or exp or ddp , DDF for fabrication or ddf

def get_lot_date():
    lot = ""
    ddp = ""

    try:
        result = ocr_model.ocr("./file_with_segmentations/Date.png", cls=True)

        date = [res[1][0] for res in result[0]]
        print("date bla cases : " , date)

        for d in date:
            if getting_date(d) is None:
                pass
            else:
                key, value = getting_date(d)
                if '/' in value and key != "lot":
                    parts = value.split('/')
                    if len(parts) > 1:
                        if len(parts[1]) > 0 and len(parts[1]) < 3:
                            parts[1] = '20' + parts[1]
                            value = '/'.join(parts)

                if key == "DDP":
                    print("value ta3 DDP {}".format(ddp))
                    ddp = parser.parse(value, dayfirst=True, fuzzy=True).date()
                if key == "lot":
                    # print("value ta3 lot {}".format(lot))
                    lot = value
                    # CHANGES !!!!!!!!!!!!!!!!!!!!
                    if lot is not None and len(lot) > 0:
                        if lot[0] == 'n':
                            lot = lot[1:]

                print(key + " " + value)

    except:
        pass
    return lot, ddp


#############################################

#getting_ppa_value part


def where_num(input_string):
    out = ''
    for ch in input_string:
        if ch.isnumeric():
            out = out + ch
    return out


def ifda(ppa):
    for p in ppa:
        check = False
        position = None
        flag = False
        for ch in p:
            if ch.isnumeric():
                flag = True
        if flag:
            p = p.lower()

            if 'da' in p:
                position = ppa.index(p.upper())
                check = True
                break
    return check, flag, position


def check_contain_only_num(input_string):
    flag = False

    if len(input_string) > 0:
        flag = True
        for ch in input_string:
            if ch != '.':
                if not ch.isnumeric():
                    flag = False
                    break
    return flag


def getting_ppa_value_from_cases(ppa):
    # ppa is the OCR OUTPUT for Champ PPA
    if len(ppa) > 0:
        for p in ppa:
            bo, n, pos = ifda(ppa)

        if bo and n:

            p = ppa[pos]
            p = p.lower()

            if 'da' in p:
                if ':' in p.split('da')[0]:

                    if 'a' in p.split('da')[0]:
                        s = p.split('da')[0].split(':')[1]
                        if '=' in s:
                            s = p.split('da')[0].split('=')[1]
                        if '-' in s:
                            s = p.split('da')[0].split('-')[1]
                        s = where_num(s)

                        if (',' not in s) and ('.' not in s):
                            if len(s) > 3:
                                s = s[:-2] + '.' + s[-2:]

                        if ',' not in s:
                            s = s.replace(",", ".")
                        return (s)

                elif '=' in p.split('da')[0]:
                    s = p.split('da')[0].split('=')[1]
                    if (',' not in s) and ('.' not in s):
                        if len(s) > 3:
                            s = s[:-2] + '.' + s[-2:]
                    if ',' not in s:
                        s = s.replace(",", ".")
                    return (s)

                elif '-' in p.split('da')[0]:

                    s = p.split('da')[0].split('-')[1]
                    s = where_num(s)

                    if (',' not in s) and ('.' not in s):
                        if len(s) > 3:
                            s = s[:-2] + '.' + s[-2:]
                    if ',' not in s:
                        s = s.replace(",", ".")
                    return (s)

                elif 'a' in p.split('da')[0]:

                    s = p.split('da')[0].split('a')[1]
                    s = where_num(s)

                    if (',' not in s) and ('.' not in s):
                        if len(s) > 3:
                            s = s[:-2] + '.' + s[-2:]
                    if ',' not in s:
                        s = s.replace(",", ".")
                    return (s)
                else:
                    s = p.split('da')[0]
                    s = where_num(s)

                    if (',' not in s) and ('.' not in s):
                        if len(s) > 3:
                            s = s[:-2] + '.' + s[-2:]
                    if ',' not in s:
                        s = s.replace(",", ".")
                    return (s)

        if not bo and n:
            for p in ppa:

                if not 'da' in p:
                    if '=' in p:
                        if '+' in p.split('=')[1]:
                            a = p.split('=')[1].split('+')[0]
                            a = where_num(a)
                            if len(a) > 3:
                                a = a[:-2] + '.' + a[-2:]

                            b = p.split('=')[1].split('+')[1]
                            b = where_num(b)
                            if len(b) >= 3:
                                b = b[:-2] + '.' + b[-2:]
                            if check_contain_only_num(a) and check_contain_only_num(b):
                                s = float(a) + float(b)
                                return (s)
                            else:
                                pass
                    if '-' in p:
                        if '+' in p.split('-')[1]:
                            a = p.split('-')[1].split('+')[0]
                            a = where_num(a)
                            if len(a) > 3:
                                a = a[:-2] + '.' + a[-2:]
                            b = p.split('-')[1].split('+')[1]
                            b = where_num(b)
                            if len(b) >= 3:
                                b = b[:-2] + '.' + b[-2:]
                            if check_contain_only_num(a) and check_contain_only_num(b):
                                s = float(a) + float(b)
                                return (s)
                            else:
                                pass
    else:
        print('print OCR has returned norhing')

    # HOW TO USE IT
    # PREDICT LI DERHA WALID F NOTEBOOK


def get_ppa_value():
    try:
        result = ocr_model.ocr("./file_with_segmentations/PPA.png", cls=True)
        ppa = [res[1][0] for res in result[0]]
        print("ppa bla cases : " , ppa)
        ppa_value = getting_ppa_value_from_cases(ppa)
        print("ppa value from fct : " , ppa_value)
        # ensure french language numbers taken in consideration
        ppa_value = ppa_value.replace(",", ".")
        if check_contain_only_num(ppa_value):
            return float(ppa_value)
        else:
            return 0.0
    except:
        return 0.0





#########################

#stock.py content

def getting_class_ids_to_output(class_ids_NDD,class_ids_PF):
    set1 = set(class_ids_NDD.tolist())
    set2 = set(class_ids_PF.tolist())

    result1 = set2 - set1
    result_to_add_to_PF = list(result1.intersection({1, 3, 4}))
    result_to_output_from_NDD = list((set1- set(result1)).intersection({1, 3, 4}))

    result2 = set1 - set2
    result_to_add_to_NDD = list(result2.intersection({0,2}))
    result_to_output_from_PF = list((set2- set(result2)).intersection({0,2}))


    class_ids_to_output_from_PF = result_to_output_from_PF + result_to_add_to_PF
    class_ids_to_output_from_NDD = result_to_output_from_NDD + result_to_add_to_NDD

    return class_ids_to_output_from_NDD , class_ids_to_output_from_PF

def segmenting_and_saving(img,masks,class_ids ,class_ids_to_output_from_):
    # Create new image with transparent background

    new_ppa = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
    new_date = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
    new_name = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
    new_dossage = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)


    for mask, class_id in zip(masks, class_ids):
        if int(class_id.item()) in class_ids_to_output_from_ :
            class_name = classes_names[str(int(class_id.item()))]

            # print('i am outputing : '+str(class_name))


            # if class_name == 'Date' or class_name ==  'Name' or class_name == 'Dossage' :
            #  Check if class name is 'Date'
            if class_name == 'Date':
                # Convert mask to boolean mask
                bool_mask_date = mask.to(torch.bool).numpy()

                # Resize boolean mask to match img shape
                bool_mask_date = cv2.resize(bool_mask_date.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                # Use boolean mask to index img array
                new_date[bool_mask_date] = img[bool_mask_date]

                cv2.imwrite(f'./{directoryPath}/Date.png', new_date)

                # cv2.imwrite(f'./{directoryPath}/without_Date.jpg', img_without_date_ppa)
            if class_name == 'Name':
                # Convert mask to boolean mask
                bool_mask_name = mask.to(torch.bool).numpy()

                # Resize boolean mask to match img shape
                bool_mask_name = cv2.resize(bool_mask_name.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                # Use boolean mask to index img array
                new_name[bool_mask_name] = img[bool_mask_name]

                cv2.imwrite(f'./{directoryPath}/Name.png', new_name)

            if class_name == 'Dossage':
                # Convert mask to boolean mask

                bool_mask_dossage = mask.to(torch.bool).numpy()

                # Resize boolean mask to match img shape
                bool_mask_dossage = cv2.resize(bool_mask_dossage.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                # Use boolean mask to index img array
                new_dossage[bool_mask_dossage] = img[bool_mask_dossage]

                cv2.imwrite(f'./{directoryPath}/Dossage.png', new_dossage)

            if class_name == 'PPA':
                    # Convert mask to boolean mask

                    bool_mask_ppa = mask.to(torch.bool).numpy()

                    # Resize boolean mask to match img shape
                    bool_mask_ppa = cv2.resize(bool_mask_ppa.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                    # Use boolean mask to index img array
                    new_ppa[bool_mask_ppa] = img[bool_mask_ppa]

                    cv2.imwrite(f'./{directoryPath}/PPA.png', new_ppa)
                    # cv2.imwrite(f'./{directoryPath}/without_PPA.jpg', img_without_date_ppa)


def drop_duplicants(class_tensor,conf_tensor):
    unique_elements, counts = torch.unique(class_tensor, return_counts=True)

    # Find the elements that appear more than once
    repeated_elements = unique_elements[counts > 1]

    # Find the positions of the repeated elements
    positions = [torch.where(class_tensor == element) for element in repeated_elements]
    positions_to_del=[]
    for pos in positions:
        positions_to_del.append(pos[0].tolist())

    new_pos = np.array(range(0,len(class_tensor )))

    for t in positions_to_del :
        for j in t :
            new_pos = np.delete(new_pos,np.where(new_pos==j))


    pos_to_keep = []
    for t in positions_to_del :
            pos_to_keep.append(conf_tensor.tolist().index(max(conf_tensor[t].tolist())))

    new_pos= np.append(new_pos,pos_to_keep)

    return new_pos



def  get_product_id_from_segmented_image(vig_color_id):

    id = 0
    name = ""
    dos = ""

    try:
        result = ocr_model.ocr("./file_with_segmentations/Name.png", cls=True)
        name = "".join([res[1][0] for res in result[0]])

        result = ocr_model.ocr("./file_with_segmentations/Dossage.png", cls=True)
        dos = "".join([res[1][0] for res in result[0]])
    except:
        pass
    print("name " , name)
    print("dos ",dos)
    try:
        id = fuzzy_best_match_id(name,dos,vig_color_id)

        if id is None :
            id = 0
    except:
        pass
    return int(id)

# def fuzzy_best_match_id(text , vig_class_id):
#
#     meds_file_path = ""
#     if vig_class_id == 0:
#         meds_file_path = "green_meds.txt"
#     elif vig_class_id == 2:
#         print("dkhal l had fichier")
#         meds_file_path = "red_meds.TXT"
#
#     best_match_id = None
#     best_match_ratio = 0
#
#     with open(meds_file_path, 'r') as file:
#         for line in file:
#             parts = line.strip().split("###")
#             if len(parts) == 2:
#                 medicine_id, medicine_name = parts[0], parts[1]
#                 ratio = fuzz.ratio(text, medicine_name)
#                 if ratio > best_match_ratio:
#                     best_match_id = medicine_id
#                     best_match_ratio = ratio
#
#     print("rana nemsho")
#     return best_match_id

def fuzzy_best_match_id(search_medicine_name, search_medicine_dosage,vig_class_id):
    search_medicine_name = search_medicine_name.upper()
    search_medicine_dosage = search_medicine_dosage.upper()

    meds_file_path = ""
    if vig_class_id == 0:
        meds_file_path = json_file
        with open(json_file, 'r') as input_file:
            data = json.load(input_file)

        best_match_name, _ = process.extractOne(search_medicine_name, data.keys())
        dosages = data[best_match_name]
        best_match_dosage, _ = process.extractOne(search_medicine_dosage, [dosage["dosage"] for dosage in dosages])

        for dosage in dosages:
            if dosage["dosage"] == best_match_dosage:
                return dosage["id"]

        return None


    elif vig_class_id == 2:
        meds_file_path = "red_meds.TXT"
        best_match_id = None
        best_match_ratio = 0
        text = search_medicine_name + " " + search_medicine_dosage

        with open(meds_file_path, 'r') as file:
            for line in file:
                parts = line.strip().split("###")
                if len(parts) == 2:
                    medicine_id, medicine_name = parts[0], parts[1]
                    ratio = fuzz.ratio(text, medicine_name)
                    if ratio > best_match_ratio:
                        best_match_id = medicine_id
                        best_match_ratio = ratio


        return best_match_id







def get_product_id_date_ppa(src):

    img = cv2.imread(src)



    # Python program to check
    # if a directory contains file




    # path of the directory

    # Check whether the specified
    # path exists or not
    isExist = os.path.exists(directoryPath)
    if not isExist :
            os.mkdir(directoryPath)

    # Comparing the returned list to empty list
    if os.listdir(directoryPath) == []:
              #print("No files found in the directory.")
              pass
    else:
        shutil.rmtree(directoryPath)
        os.mkdir(directoryPath)
    start = time.time()
    # Pass the frame to the YOLO model for prediction

    results = model.predict(source=src,conf=0.6,show=False, save_crop=False , save = False,retina_masks=True , verbose=False)
    print("model 1 took {}".format(time.time() - start))
    #THE CODITION IS FOR IF HE IS DETECTING A VIGNETTE
    if (len(results[0].boxes.cls)>0):
        color_id  =results[0].boxes.cls[0]
        print("color id is {}".format(color_id))
        #THE CODITION IS FOR IF HE IS DETECTING A Green or Red VIGNETTE
        if (color_id==0) or (color_id==2):
            #color contains Green or Red
            color = vignette_color[str(int(color_id))]
            for r in results:
                    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGBA)
                    # Get segmentation masks
                    masks = r.masks.data
                    # Create new image with transparent background
                    new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                    for mask in masks:
                        # Convert mask to boolean mask
                        bool_mask = mask.to(torch.bool).numpy()
                        # Resize boolean mask to match img shape
                        bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)
                        # Use boolean mask to index img array
                        new_img[bool_mask] = img[bool_mask]
                    # Save new image
                    output_path = f'./{src}_segmented.png'

                    cv2.imwrite(output_path, new_img)

                    def run_model_champs_NAME_DATE_DOS():
                        global results_NDD
                        results_NDD = model_champs_NAME_DATE_DOS.predict(source=output_path, show=False, save=False,
                                                                         show_labels=False, show_conf=False, conf=0.1,
                                                                         save_txt=False, save_crop=False,
                                                                         retina_masks=True,
                                                                         save_conf=True, verbose=False)

                    def run_model_champs_PPA_FORM():
                        global results_PF
                        results_PF = model_champs_PPA_FORM.predict(source=output_path, show=False, save=False,
                                                                   show_labels=False, show_conf=False, conf=0.1,
                                                                   save_txt=False, save_crop=False, retina_masks=True,
                                                                   save_conf=True, verbose=False)

                    # Create threads
                    thread_NDD = threading.Thread(target=run_model_champs_NAME_DATE_DOS)
                    thread_PF = threading.Thread(target=run_model_champs_PPA_FORM)

                    # Start threads
                    thread_NDD.start()
                    thread_PF.start()

                    # Wait for threads to complete
                    thread_NDD.join()
                    thread_PF.join()

                    #DELETING GENERATED SEGMENTED IMAGE OF THE VIGNETTE, bcs WE DONT NEED IT





                    conf_tensor_NDD  = results_NDD[0].boxes.conf
                    class_tensor_NDD = results_NDD[0].boxes.cls


                    conf_tensor_PF  = results_PF[0].boxes.conf
                    class_tensor_PF = results_PF[0].boxes.cls
                    # Assuming you have a torch tensor `x`


                    new_pos_NDD = drop_duplicants(class_tensor_NDD,conf_tensor_NDD)

                    new_pos_PF = drop_duplicants(class_tensor_PF,conf_tensor_PF)

                    cls_NDD=results_NDD[0].boxes.cls[new_pos_NDD]
                    data_NDD = results_NDD[0].masks.data[new_pos_NDD]

                    cls_PF=results_PF[0].boxes.cls[new_pos_PF]
                    data_PF = results_PF[0].masks.data[new_pos_PF]




                    #THE CONDITION BELOW IS OPTIONAL
                    # results[0].boxes.cls == 4 TO CHECK THAT THE NAME HAS BEEN DETECTED , len(results[0].boxes.cls)>=3 TO CHECK IF AT LEAST 3 CHAMPS HAVE BEEN DETECTED

                    if (torch.any(results_NDD[0].boxes.cls == 4)) and (len(results_NDD[0].boxes.cls)>=3) :
                        #print("I HAVE CHAMPS")

                        output_dir = f'./file_with_segmentations'
                        # os.mkdir(output_dir)


                        masks_NDD = data_NDD
                        masks_PF = data_PF


                        # Get class names of detected objects
                        class_ids_NDD =  cls_NDD
                        class_ids_PF = cls_PF


                        class_ids_to_output_from_NDD ,class_ids_to_output_from_PF = getting_class_ids_to_output(class_ids_NDD,class_ids_PF)

                        # print(class_ids_to_output_from_NDD)
                        # print(class_ids_to_output_from_PF)
                        # Iterate over segmentation masks and class names

                        # Iterate over segmentation masks and class names

                        segmenting_and_saving(img, masks_NDD, class_ids_NDD, class_ids_to_output_from_NDD)
                        segmenting_and_saving(img, masks_PF, class_ids_PF, class_ids_to_output_from_PF)

                        # img_without_date_ppa = segmenting_and_saving(img,masks_NDD,class_ids_NDD ,class_ids_to_output_from_NDD)
                        # img_without_date_ppa = segmenting_and_saving(img_without_date_ppa,masks_PF,class_ids_PF ,class_ids_to_output_from_PF)
                        #
                        # cv2.imwrite(f'./{directoryPath}/without_Date_PPA.png', img_without_date_ppa)



                        location = "./"
                        path = os.path.join(location, output_path)
                        os.remove(path)

                    else:
                        pass
                        #print('I didn't catch it the name or i didnt catch at lesat 3 champs  , pls retry !')
        else:
            pass
            #print(' DETECTED A WHITE VIGNETTE')




    id = get_product_id_from_segmented_image(results[0].boxes.cls[0])
    ppa = get_ppa_value()
    lot,ddp = get_lot_date()

    return id,ppa,lot,ddp


##############################

#new_product.py


def segmenting_and_saving_product(img, masks, class_ids, class_ids_to_output_from_, output_dir=directoryPath):
    def process_segment(mask, class_id):
        if int(class_id.item()) in class_ids_to_output_from_:
            class_name = classes_names[str(int(class_id.item()))]

            # CONDITION HERE !!!!!!
            if class_name == 'Name' or class_name == 'Forme' or class_name == 'Dossage':

                new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                bool_mask = mask.to(torch.bool).numpy()
                bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)
                new_img[bool_mask] = img[bool_mask]
                check_file = os.path.exists(f'{output_dir}/{class_name}.jpg')
                if check_file:
                    cv2.imwrite(f'{output_dir}/{class_name}_1.jpg', new_img)
                else:
                    cv2.imwrite(f'{output_dir}/{class_name}.jpg', new_img)

    with concurrent.futures.ThreadPoolExecutor() as executor:
        futures = [executor.submit(process_segment, mask, class_id) for mask, class_id in zip(masks, class_ids)]
        concurrent.futures.wait(futures)


def drop_duplicants(class_tensor, conf_tensor):
    dup_start = time.time()
    unique_elements, counts = torch.unique(class_tensor, return_counts=True)

    # Find the elements that appear more than once
    repeated_elements = unique_elements[counts > 1]

    # Find the positions of the repeated elements
    positions = [torch.where(class_tensor == element) for element in repeated_elements]
    positions_to_del = []
    for pos in positions:
        positions_to_del.append(pos[0].tolist())

    new_pos = np.array(range(0, len(class_tensor)))

    for t in positions_to_del:
        for j in t:
            new_pos = np.delete(new_pos, np.where(new_pos == j))

    pos_to_keep = []
    for t in positions_to_del:
        pos_to_keep.append(conf_tensor.tolist().index(max(conf_tensor[t].tolist())))

    new_pos = np.append(new_pos, pos_to_keep)

    dup_end = time.time()

    return new_pos



# BEGINS HERE




def new_product(src):
    # Check whether the specified
    # path exists or not
    isExist = os.path.exists(directoryPath)
    if not isExist:
        os.mkdir(directoryPath)

    # Comparing the returned list to empty list
    if os.listdir(directoryPath) == []:
        # print("No files found in the directory.")
        pass
    else:
        shutil.rmtree(directoryPath)
        os.mkdir(directoryPath)

    img = cv2.imread(src)

    # Pass the frame to the YOLO model for prediction
    results = model.predict(source=src, conf=0.7, show=False, save_crop=False, save=False, retina_masks=True, verbose=False)

    loop_start = time.time()
    # THE CODITION IS FOR IF HE IS DETECTING A VIGNETTE
    if (len(results[0].boxes.cls) > 0):
        # TO check if vignette is red !
        if (results[0].boxes.cls[0] == 2):

            for r in results:

                img = cv2.cvtColor(img, cv2.COLOR_BGR2RGBA)
                # Get segmentation masks
                masks = r.masks.data

                # Create new image with transparent background
                new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                for mask in masks:
                    # Convert mask to boolean mask
                    bool_mask = mask.to(torch.bool).numpy()

                    # Resize boolean mask to match img shape
                    bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)

                    # Use boolean mask to index img array
                    new_img[bool_mask] = img[bool_mask]
                    # Save new image
                    output_path = f'./{src}_segmented.png'
                    cv2.imwrite(output_path, new_img)
                    m2all_start = time.time()


                    def run_model_champs_NAME_DATE_DOS():
                        global results_NDD
                        results_NDD = model_champs_NAME_DATE_DOS.predict(source=output_path, show=False, save=False,
                                                                         show_labels=False, show_conf=False, conf=0.1,
                                                                         save_txt=False, save_crop=False, retina_masks=True,
                                                                         save_conf=True, verbose=False)


                    def run_model_champs_PPA_FORM():
                        global results_PF
                        m22_start = time.time()
                        results_PF = model_champs_PPA_FORM.predict(source=output_path, show=False, save=False,
                                                                   show_labels=False, show_conf=False, conf=0.1,
                                                                   save_txt=False, save_crop=False, retina_masks=True,
                                                                   save_conf=True, verbose=False)


                    # Create threads
                    thread_NDD = threading.Thread(target=run_model_champs_NAME_DATE_DOS)
                    thread_PF = threading.Thread(target=run_model_champs_PPA_FORM)

                    # Start threads
                    thread_NDD.start()
                    thread_PF.start()

                    # Wait for threads to complete
                    thread_NDD.join()
                    thread_PF.join()

                    # DELETING GENERATED SEGMENTED IMAGE OF THE VIGNETTE, bcs WE DONT NEED IT

                    location = "./"
                    path = os.path.join(location, output_path)
                    os.remove(path)

                    conf_tensor_NDD = results_NDD[0].boxes.conf
                    class_tensor_NDD = results_NDD[0].boxes.cls
                    # print(conf_tensor_NDD)
                    # print(class_tensor_NDD)

                    conf_tensor_PF = results_PF[0].boxes.conf
                    class_tensor_PF = results_PF[0].boxes.cls
                    # Assuming you have a torch tensor `x`

                    new_pos_NDD = drop_duplicants(class_tensor_NDD, conf_tensor_NDD)

                    new_pos_PF = drop_duplicants(class_tensor_PF, conf_tensor_PF)

                    cls_NDD = results_NDD[0].boxes.cls[new_pos_NDD]
                    data_NDD = results_NDD[0].masks.data[new_pos_NDD]

                    cls_PF = results_PF[0].boxes.cls[new_pos_PF]
                    data_PF = results_PF[0].masks.data[new_pos_PF]

                    # THE CONDITION BELOW IS OPTIONAL
                    # results[0].boxes.cls == 4 TO CHECK THAT THE NAME HAS BEEN DETECTED , len(results[0].boxes.cls)>=3 TO CHECK IF AT LEAST 3 CHAMPS HAVE BEEN DETECTED

                    if (torch.any(results_NDD[0].boxes.cls == 4)) and (len(results_NDD[0].boxes.cls) >= 3):
                        # print("I HAVE CHAMPS")

                        output_dir = f'./file_with_segmentations'
                        # os.mkdir(output_dir)

                        masks_NDD = data_NDD
                        masks_PF = data_PF

                        # Get class names of detected objects
                        class_ids_NDD = cls_NDD
                        class_ids_PF = cls_PF

                        class_ids_to_output_from_NDD, class_ids_to_output_from_PF = getting_class_ids_to_output(
                            class_ids_NDD,
                            class_ids_PF)

                        # print(class_ids_to_output_from_NDD)
                        # print(class_ids_to_output_from_PF)
                        # Iterate over segmentation masks and class names

                        segmenting_and_saving_product(img, masks_NDD, class_ids_NDD, class_ids_to_output_from_NDD)
                        segmenting_and_saving_product(img, masks_PF, class_ids_PF, class_ids_to_output_from_PF)


                    else:
                        # print('I didnt catch it the name or i didnt catch at lesat 3 champs  , pls retry !')
                        pass
            else:
                # print('it must be a red vignette to add new product')
                pass

    paths = ["./file_with_segmentations/Name.jpg", "./file_with_segmentations/Dossage.jpg",
             "./file_with_segmentations/Forme.jpg"]


    name = ""
    dos=""
    form=""
    try:
        result = ocr_model.ocr(paths[0], cls=True)
        name = "".join([res[1][0] for res in result[0]])
    except:

        pass
    try:
        result = ocr_model.ocr(paths[1], cls=True)
        dos = "".join([res[1][0] for res in result[0]])
    except:
        pass

    try:
        result = ocr_model.ocr(paths[2], cls=True)
        form = "".join([res[1][0] for res in result[0]])
    except:
        pass

    return name,dos,form








#########################
#vente.py

def segmenting_and_saving_vente(img, masks, class_ids, class_ids_to_output_from_, output_dir=directoryPath):
    def process_segment(mask, class_id):
        if int(class_id.item()) in class_ids_to_output_from_:
            class_name = classes_names[str(int(class_id.item()))]
            # CONDITION HERE !!!!!!
            if class_name != 'Forme':

                new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                bool_mask = mask.to(torch.bool).numpy()
                bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)
                new_img[bool_mask] = img[bool_mask]
                check_file = os.path.exists(f'{output_dir}/{class_name}.png')
                if check_file:
                    cv2.imwrite(f'{output_dir}/{class_name}_1.png', new_img)
                else:
                    cv2.imwrite(f'{output_dir}/{class_name}.png', new_img)

    with concurrent.futures.ThreadPoolExecutor() as executor:
        futures = [executor.submit(process_segment, mask, class_id) for mask, class_id in zip(masks, class_ids)]
        concurrent.futures.wait(futures)


def vente(src):

    img = cv2.imread(src)

    isExist = os.path.exists(directoryPath)
    if not isExist :
            os.mkdir(directoryPath)

    # Comparing the returned list to empty list
    if os.listdir(directoryPath) == []:
              #print("No files found in the directory.")
              pass
    else:
        shutil.rmtree(directoryPath)
        os.mkdir(directoryPath)

    # Pass the frame to the YOLO model for prediction
    results = model.predict(source=src,conf=0.7,show=False, save_crop=False , save = False,retina_masks=True , verbose=False)

    #THE CODITION IS FOR IF HE IS DETECTING A VIGNETTE
    if (len(results[0].boxes.cls)>0):
        color_id  =results[0].boxes.cls[0]
        #THE CODITION IS FOR IF HE IS DETECTING A Green or R ed VIGNETTE
        if (color_id==0) or (color_id==2):
            #color contains Green or Red
            color = vignette_color[str(int(color_id))]
            for r in results:
                    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGBA)
                    # Get segmentation masks
                    masks = r.masks.data
                    # Create new image with transparent background
                    new_img = np.zeros((img.shape[0], img.shape[1], 4), dtype=np.uint8)
                    for mask in masks:
                        # Convert mask to boolean mask
                        bool_mask = mask.to(torch.bool).numpy()
                        # Resize boolean mask to match img shape
                        bool_mask = cv2.resize(bool_mask.astype(np.uint8), (img.shape[1], img.shape[0])).astype(bool)
                        # Use boolean mask to index img array
                        new_img[bool_mask] = img[bool_mask]
                    # Save new image
                    output_path = f'./{src}_segmented.png'

                    cv2.imwrite(output_path, new_img)

                    def run_model_champs_NAME_DATE_DOS():
                        global results_NDD
                        results_NDD = model_champs_NAME_DATE_DOS.predict(source=output_path, show=False, save=False,
                                                                         show_labels=False, show_conf=False, conf=0.1,
                                                                         save_txt=False, save_crop=False,
                                                                         retina_masks=True,
                                                                         save_conf=True, verbose=False)

                    def run_model_champs_PPA_FORM():
                        global results_PF
                        results_PF = model_champs_PPA_FORM.predict(source=output_path, show=False, save=False,
                                                                   show_labels=False, show_conf=False, conf=0.1,
                                                                   save_txt=False, save_crop=False, retina_masks=True,
                                                                   save_conf=True, verbose=False)

                    # Create threads
                    thread_NDD = threading.Thread(target=run_model_champs_NAME_DATE_DOS)
                    thread_PF = threading.Thread(target=run_model_champs_PPA_FORM)

                    # Start threads
                    thread_NDD.start()
                    thread_PF.start()

                    # Wait for threads to complete
                    thread_NDD.join()
                    thread_PF.join()

                    #DELETING GENERATED SEGMENTED IMAGE OF THE VIGNETTE, bcs WE DONT NEED IT





                    conf_tensor_NDD  = results_NDD[0].boxes.conf
                    class_tensor_NDD = results_NDD[0].boxes.cls


                    conf_tensor_PF  = results_PF[0].boxes.conf
                    class_tensor_PF = results_PF[0].boxes.cls
                    # Assuming you have a torch tensor `x`


                    new_pos_NDD = drop_duplicants(class_tensor_NDD,conf_tensor_NDD)

                    new_pos_PF = drop_duplicants(class_tensor_PF,conf_tensor_PF)

                    cls_NDD=results_NDD[0].boxes.cls[new_pos_NDD]
                    data_NDD = results_NDD[0].masks.data[new_pos_NDD]

                    cls_PF=results_PF[0].boxes.cls[new_pos_PF]
                    data_PF = results_PF[0].masks.data[new_pos_PF]




                    #THE CONDITION BELOW IS OPTIONAL
                    # results[0].boxes.cls == 4 TO CHECK THAT THE NAME HAS BEEN DETECTED , len(results[0].boxes.cls)>=3 TO CHECK IF AT LEAST 3 CHAMPS HAVE BEEN DETECTED

                    if (torch.any(results_NDD[0].boxes.cls == 4)) and (len(results_NDD[0].boxes.cls)>=3) :
                        #print("I HAVE CHAMPS")

                        output_dir = f'./file_with_segmentations'
                        # os.mkdir(output_dir)


                        masks_NDD = data_NDD
                        masks_PF = data_PF


                        # Get class names of detected objects
                        class_ids_NDD =  cls_NDD
                        class_ids_PF = cls_PF


                        class_ids_to_output_from_NDD ,class_ids_to_output_from_PF = getting_class_ids_to_output(class_ids_NDD,class_ids_PF)

                        # Iterate over segmentation masks and class names

                        # Iterate over segmentation masks and class names

                        segmenting_and_saving_vente(img, masks_NDD, class_ids_NDD, class_ids_to_output_from_NDD)
                        segmenting_and_saving_vente(img, masks_PF, class_ids_PF, class_ids_to_output_from_PF)



                        location = "./"
                        path = os.path.join(location, output_path)
                        os.remove(path)

                    else:
                        pass
                        #print('I didn't catch it the name or i didnt catch at lesat 3 champs  , pls retry !')
        else:
            pass
            #print(' DETECTED A WHITE VIGNETTE')




    id = get_product_id_from_segmented_image(results[0].boxes.cls[0])
    lot,ddp = get_lot_date()

    return id,lot,ddp




#################################


class ProductRequest(BaseModel):
    path: str


# Instantiating a FastAPI object handling all API routes
app = FastAPI()
# Creating a GET endpoint at the root path
@app.post("/product")
async def get_product(req: ProductRequest):
    s = time.time()

    print(req)
    name, dos, form = new_product(req.path)
    print(f"from api get_product it took {time.time()-s}")


    return {"name": name, "dos": dos, "forme": form}
@app.post("/vente")
async def get_vente(req: ProductRequest):
    s = time.time()

    print(req)
    id,lot,ddp = vente(req.path)
    print(f"from api get_vente it took {time.time()-s}")

    return {"product_id": id,"lot":lot, "date": ddp}

@app.post("/stock")
async def get_stock(req: ProductRequest):
    s = time.time()
    print(req)
    id,ppa,lot,ddp = get_product_id_date_ppa(req.path)
    print(f"from api get_stock it took {time.time()-s}")
    return {"product_id": id, "ppa": ppa,"lot":lot, "date": ddp}
# Async method returning a JSON response autmatically

