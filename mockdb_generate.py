import random
states=['Maharashtra','Delhi','Karnataka','Gujarat','Tamil Nadu','Uttar Pradesh','Rajasthan','West Bengal','Punjab','Haryana','Kerala','Bihar','Odisha','Telangana']
types=['Car / Jeep','Two Wheeler','HMV — Truck / Bus','Electric Vehicle']
makes=['Tata Nexon','Honda Activa','Hyundai Creta','Mahindra Bolero','Maruti Suzuki Alto','Toyota Innova','Kia Seltos','MG Hector','Bajaj Pulsar','TVS Jupiter','Hero Splendor','Volvo FH','Ashok Leyland Ecomet','Tesla Model 3']
fuels=['Petrol','Diesel','CNG','Electric','Hybrid']
owners=['Amit Sharma','Suresh Kumar','Priya Verma','Vikram Singh','Anita Desai','Rajesh Patel','Sunita Rao','Deepak Joshi','Meera Nair','Sonal Gupta','Karan Mehta','Nikhil Jain','Rohit Yadav','Sneha Iyer','Aditya Das','Neha Kapoor','Rajat Verma','Pooja Kaur','Amrita Sen','Manish Khanna']
vrn_prefix=['MH','DL','KA','GJ','TN','UP','RJ','WB','PB','HR','KL','BR','OD','TS']
records=[]
for i in range(100):
    prefix=vrn_prefix[i%len(vrn_prefix)]
    region_code=str(1 + (i//len(vrn_prefix))%99).zfill(2)
    alpha=''.join(random.choice('ABCDEFGHIJKLMNOPQRSTUVWXYZ') for _ in range(2))
    digits=str(1000 + (i%9000))
    vrn=f'{prefix}-{region_code} {alpha} {digits}'
    owner=owners[i%len(owners)]
    driver=owner
    state=states[i%len(states)]
    type_=types[i%len(types)]
    make=makes[i%len(makes)]
    fuel=fuels[i%len(fuels)]
    ins='Valid' if i%5 != 0 else 'Expired'
    puc='Valid' if i%4 != 0 else 'Expired'
    rc='Active' if i%3 != 0 else 'Expired'
    blacklist='Yes' if i%10 == 0 else 'No'
    regDate=f'{random.randint(1,28):02d}/{random.randint(1,12):02d}/{2018+random.randint(0,6)}'
    prev=i%8
    dl=f'{prefix}{region_code} {2010+i%10:04d}{random.randint(100,999):03d}'
    mobile='9' + str(100000000 + i)
    rec={'vrn':vrn,'type':type_,'owner':owner,'state':state,'make':make,'fuel':fuel,'insStatus':ins,'pucStatus':puc,'rcStatus':rc,'regDate':regDate,'prevChallans':prev,'blacklist':blacklist,'driver':driver,'dl':dl,'mobile':mobile}
    records.append((vrn,rec))
with open('mockdb100.txt','w',encoding='utf-8') as f:
    f.write('        const mockDB = {\n')
    for vrn,rec in records:
        f.write(f"            '{vrn}': {{\n")
        for k,v in rec.items():
            if isinstance(v,str):
                f.write(f"                {k}: '{v}',\n")
            else:
                f.write(f"                {k}: {v},\n")
        f.write('            },\n')
    f.write('        };\n')
