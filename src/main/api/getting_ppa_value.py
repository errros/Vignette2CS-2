import cv2  # opencv
import os  # folder directory navigation
import concurrent.futures
from paddleocr import PaddleOCR


ocr_model = PaddleOCR(lang='fr', use_angle_cls=True, show_log=False)

def where_num(input_string) :
  out= ''
  for ch in input_string:
      if ch.isnumeric():
          out =out+ch
  return out

def ifda (ppa):
  for p in ppa:
    check=False
    position=None
    flag = False
    for ch in p:
      if ch.isnumeric():
          flag = True
    if flag : 
      p = p.lower()

      if 'da' in p:
          position = ppa.index(p.upper())
          check=True
          break
  return check ,flag,  position

def check_contain_only_num(input_string) :
  flag = False

  if len (input_string) >0 : 
    flag = True
    for ch in input_string:
      if ch !='.':
        if not ch.isnumeric():
            flag = False
            break
  return flag


def getting_ppa_value_from_cases (ppa) :
  # ppa is the OCR OUTPUT for Champ PPA
  if len(ppa)>0 :   
    for p in ppa :
      bo ,n, pos = ifda(ppa)

    if bo and n: 

      p = ppa[pos]
      p = p.lower()


      if 'da' in p:
        if ':' in p.split('da')[0]:

          if 'a' in p.split('da')[0] :
            s = p.split('da')[0].split(':')[1]
            if '=' in s:
              s = p.split('da')[0].split('=')[1]
            if '-' in s:
              s = p.split('da')[0].split('-')[1]
            s = where_num(s)

            if (','  not in s) and  ('.' not in s) : 
              if len(s)>3:
                s  =s[:-2] + '.' + s[-2:]
            
            if ','  not in s : 
              s = s.replace(",", ".")
            return(s)
            
        elif '=' in p.split('da')[0]:
          s = p.split('da')[0].split('=')[1]
          if (','  not in s) and  ('.' not in s) : 
            if len(s)>3:
              s  =s[:-2] + '.' + s[-2:]
          if ','  not in s : 
            s = s.replace(",", ".")
          return(s)

        elif '-' in p.split('da')[0]:

          s = p.split('da')[0].split('-')[1]
          s = where_num(s)

          if (','  not in s) and  ('.' not in s) : 
            if len(s)>3:
              s  =s[:-2] + '.' + s[-2:]
          if ','  not in s : 
            s = s.replace(",", ".")
          return(s)

        elif 'a' in p.split('da')[0]:

          s = p.split('da')[0].split('a')[1]
          s = where_num(s)

          if (','  not in s) and  ('.' not in s) : 
            if len(s)>3:
              s  =s[:-2] + '.' + s[-2:]
          if ','  not in s : 
            s = s.replace(",", ".")
          return(s)
        else : 
          s = p.split('da')[0]
          s = where_num(s)

          if (','  not in s) and  ('.' not in s) : 
            if len(s)>3:
              s  =s[:-2] + '.' + s[-2:]
          if ','  not in s : 
            s = s.replace(",", ".")
          return(s)
        
    if not bo and n : 
      for p in ppa :

        if not 'da' in p : 
          if  '=' in p:
            if '+' in p.split('=')[1] :
              a = p.split('=')[1].split('+')[0]
              a = where_num(a)
              if len(a)>3:
                a  =a[:-2] + '.' + a[-2:]

              b = p.split('=')[1].split('+')[1]
              b = where_num(b)
              if len(b)>=3:
                b  =b[:-2] + '.' + b[-2:]
              if check_contain_only_num(a) and check_contain_only_num(b):
                s = float(a)+float(b) 
                return(s)
              else : pass
          if  '-' in p:
            if '+' in p.split('-')[1] :
              a = p.split('-')[1].split('+')[0]
              a = where_num(a)
              if len(a)>3:
                a  =a[:-2] + '.' + a[-2:]
              b = p.split('-')[1].split('+')[1]
              b = where_num(b)
              if len(b)>=3:
                b  =b[:-2] + '.' + b[-2:]
              if check_contain_only_num(a) and check_contain_only_num(b):
                s = float(a)+float(b) 
                return(s)
              else : pass
  else :print('print OCR has returned norhing')



  #HOW TO USE IT 
  #PREDICT LI DERHA WALID F NOTEBOOK



def get_ppa_value():
  try:
    result = ocr_model.ocr("./file_with_segmentations/PPA.png", cls=True)
    ppa = [res[1][0] for res in result[0]]
    ppa_value = getting_ppa_value_from_cases(ppa)
    #ensure french language numbers taken in consideration
    ppa_value = ppa_value.replace(",",".")
    if check_contain_only_num(ppa_value):
      return float(ppa_value)
    else:
      return 0.0
  except:
    return 0.0