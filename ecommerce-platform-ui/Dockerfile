FROM node:18-alpine as build

WORKDIR /ecommerce-platform-ui/

RUN apk update && \
    apk add --no-cache python3 make g++

COPY package.json package-lock.json /ecommerce-platform-ui/
RUN npm install

COPY . /ecommerce-platform-ui/

FROM node:18-alpine

WORKDIR /ecommerce-platform-ui/

COPY --from=build /ecommerce-platform-ui/ /ecommerce-platform-ui/

EXPOSE 8080

CMD ["npm", "start", "--", "--host", "0.0.0.0", "--port", "8080"]
