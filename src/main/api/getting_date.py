import cv2  # opencv
import os  # folder directory navigation
import concurrent.futures
from paddleocr import PaddleOCR

from dateutil import parser

ocr_model = PaddleOCR(lang='fr', use_angle_cls=True, show_log=False)
def symbol(part , sym):
  ddf = part.split(sym)[1]
  if len(ddf)>2 and ddf[2]=='1':
    ddf = ddf[:2] + '/' + ddf[3:]
    return ddf
  if '-' in ddf:
    ddf = ddf.replace("-", "/")
    return ddf

  else : 
    for month, value in month_mapping.items():
      if month in ddf :
        ddf = ddf.replace(month, value)
    return ddf

def gen(d,s):
  if ':' in d:
    resultat = symbol(d, ':')
    return resultat
      
  elif '.' in d:
    resultat = symbol(d, '.')
    return resultat
  else : 
    resultat = symbol(d, s)
    return resultat

month_mapping = {
            'jan': '01/', 'feb': '02/', 'mar': '03/', 'apr': '04/', 'mai': '05/', 'jun': '06/',
            'jul': '07/', 'aug': '08/', 'sep': '09/', 'oct': '10/', 'nov': '11/', 'dec': '12/'
        }

def getting_date(d): 
  if len(d)>0 :   
    d = d.lower()
    d = d.replace(" ", "")
    
    if ('ot' in d) or ('0t' in d):
      if ':' in d:
        final = d.split(':')[1]
        return ('lot' , final)

      elif '.' in d:
        final = d.split('.')[1]

        return ('lot' , final)
      else : 
        final = d.split('t')[1]
        return ('lot' , final)

    if 'df' in d:
      final =  gen(d,'f') 
      return ('DDF' , final)

    if 'dp' in d:
      final =  gen(d,'p') 
      return ('DDP' , final)

    if 'ab' in d:
      final =  gen(d,'b') 
      return ('DDF' , final)

    if 'er' in d:
      final =  gen(d,'r') 
      return ('DDP' , final)

    if 'xp' in d:
      final =  gen(d,'p') 
      return ('DDP' , final)



# HOW TO USE IT 
#date is the OCR output , predict te3 walid f Notebook
#returns a key and value , Key can be 'Lot' for LOT , DDP for peremption or exp or ddp , DDF for fabrication or ddf

def get_lot_date():
  lot = ""
  ddp = ""

  try:
    result = ocr_model.ocr("./file_with_segmentations/Date.png", cls=True)

    date = [res[1][0] for res in result[0]]
    #print(date)

    for d in date:
      if getting_date(d) is None:
        pass
      else:
          key , value = getting_date(d)
          if '/' in value:
            parts = value.split('/')
            if len(parts)>1 :
              if len(parts[1]) > 0 and len(parts[1]) < 3:
                  parts[1] = '20' + parts[1]
                  value = '/'.join(parts)

          if key == "DDP":
            #print("value ta3 DDP {}".format(ddp))
            ddp = parser.parse(value, dayfirst=True, fuzzy=True).date()
          if key == "lot":
            #print("value ta3 lot {}".format(lot))
            lot = value
          #print(key + " " + value)

  except:
    pass
  return lot,ddp