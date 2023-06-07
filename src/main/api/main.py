from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from new_product import new_product
from stock import get_product_id_date_ppa

class ProductRequest(BaseModel):
    path: str


# Instantiating a FastAPI object handling all API routes
app = FastAPI()
# Creating a GET endpoint at the root path
@app.post("/product")
async def get_product(req: ProductRequest):
    print(req)
    name, dos, form = new_product(req.path)

    return {"name": name, "dos": dos, "forme": form}

@app.post("/stock")
async def get_stock(req: ProductRequest):
    print(req)
    id,ppa,lot,ddp = get_product_id_date_ppa(req.path)

    return {"product_id": id, "ppa": ppa,"lot":lot, "date": ddp}
# Async method returning a JSON response autmatically

