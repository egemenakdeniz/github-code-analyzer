FROM node:22

# Uygulama klasörünü ayarla
WORKDIR /app

# package.json ve package-lock.json kopyala
COPY package*.json ./

# Bağımlılıkları kur
RUN npm install

# Tüm dosyaları kopyala
COPY . .

# Build işlemini yap
RUN npm run build

# Geliştirme için Vite server başlat
CMD ["npm", "run", "dev", "--", "--host"]