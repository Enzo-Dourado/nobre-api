INSERT INTO products(id,slug,name,category,category_label,price,old_price,image_url,description) VALUES
(101,'cardiga-trancado-musgo','Cardigã Trançado Musgo','frio','Roupas de Frio',349.90,NULL,'https://picsum.photos/seed/nobre-101/600/800','Tricô trançado em algodão pesado, gola V e acabamento canelado nos punhos.'),
(102,'moletom-canguru-grafite','Moletom Canguru Grafite','frio','Roupas de Frio',279.90,329.90,'https://picsum.photos/seed/nobre-102/600/800','Moletom flanelado por dentro, bolso canguru e capuz forrado.'),
(103,'jaqueta-corta-vento-nautica','Jaqueta Corta-Vento Náutica','frio','Roupas de Frio',429.90,NULL,'https://picsum.photos/seed/nobre-103/600/800','Tecido impermeável leve, forro xadrez e capuz destacável.'),
(104,'blazer-alfaiataria-verde','Blazer Alfaiataria Verde-Caça','frio','Roupas de Frio',599.90,NULL,'https://picsum.photos/seed/nobre-104/600/800','Blazer de corte reto em lã fria.'),
(105,'camiseta-gola-careca-essencial','Camiseta Gola Careca Essencial','camisetas','Camisetas',99.90,NULL,'https://picsum.photos/seed/nobre-105/600/800','Malha 100% algodão penteado.'),
(106,'camiseta-listrada-regata-clube','Camiseta Listrada Clube','camisetas','Camisetas',109.90,NULL,'https://picsum.photos/seed/nobre-106/600/800','Listras finas em jérsei leve.'),
(107,'camiseta-estampa-brasao','Camiseta Estampa Brasão','camisetas','Camisetas',119.90,139.90,'https://picsum.photos/seed/nobre-107/600/800','Estampa localizada do brasão Nobre.'),
(108,'camisa-polo-piquet-classica','Camisa Polo Piquet Clássica','polos','Camisas Polo',189.90,NULL,'https://picsum.photos/seed/nobre-108/600/800','Piquet 100% algodão.'),
(109,'camisa-polo-manga-longa','Camisa Polo Manga Longa','polos','Camisas Polo',219.90,NULL,'https://picsum.photos/seed/nobre-109/600/800','Polo clássica de manga longa.'),
(110,'calca-sarja-reta-bege','Calça Sarja Reta Bege','calcas','Calças',249.90,NULL,'https://picsum.photos/seed/nobre-110/600/800','Sarja encorpada com elastano.'),
(111,'calca-alfaiataria-preta','Calça Alfaiataria Preta','calcas','Calças',269.90,NULL,'https://picsum.photos/seed/nobre-111/600/800','Modelagem slim de alfaiataria.'),
(112,'calca-jeans-slim-indigo','Calça Jeans Slim Índigo','calcas','Calças',259.90,299.90,'https://picsum.photos/seed/nobre-112/600/800','Jeans índigo lavado.'),
(113,'bermuda-sarja-listrada','Bermuda Sarja Listrada','bermudas','Bermudas',169.90,NULL,'https://picsum.photos/seed/nobre-113/600/800','Bermuda de sarja com listras discretas.'),
(114,'bermuda-tactel-esportiva','Bermuda Tactel Esportiva','bermudas','Bermudas',139.90,NULL,'https://picsum.photos/seed/nobre-114/600/800','Tactel leve de secagem rápida.'),
(115,'bone-aba-curva-brasao','Boné Aba Curva Brasão','acessorios','Acessórios',89.90,NULL,'https://picsum.photos/seed/nobre-115/600/800','Sarja encorpada e fivela traseira.'),
(116,'cinto-couro-fivela-latao','Cinto Couro Fivela Latão','acessorios','Acessórios',129.90,NULL,'https://picsum.photos/seed/nobre-116/600/800','Couro legítimo e fivela em latão.');
SELECT setval('products_id_seq',(SELECT max(id) FROM products));
INSERT INTO product_sizes(product_id,size) SELECT id,s FROM products CROSS JOIN unnest(ARRAY['P','M','G','GG']) s;
DELETE FROM product_sizes WHERE product_id=115; INSERT INTO product_sizes VALUES(115,'Único');
DELETE FROM product_sizes WHERE product_id=116; INSERT INTO product_sizes VALUES(116,'P/M'),(116,'G/GG');
