CREATE TABLE IF NOT EXISTS "Empresas" (
	"id"	INTEGER AUTOINCREMENT PRIMARY KEY,
	"nombre"	TEXT NOT NULL,
	"descripcion"	TEXT,
	"total_acciones"	INTEGER NOT NULL,
	"valor_accion"	NUMERIC NOT NULL,
	"marca"	TEXT NOT NULL DEFAULT 'XXX',
	"sector"	TEXT NOT NULL DEFAULT 'GLOBAL'
);
CREATE TABLE IF NOT EXISTS "Partidas_guardadas" (
	"id"	INTEGER AUTOINCREMENT PRIMARY KEY,
	"usuario_id"	INTEGER NOT NULL,
	"dinero"	NUMERIC NOT NULL,
	"anyo"	INTEGER NOT NULL,
	"mes"	INTEGER NOT NULL,
	"dia"	INTEGER NOT NULL,
	"hora"	INTEGER NOT NULL,
	"minuto"	INTEGER NOT NULL,
	CONSTRAINT "partida_usuario" FOREIGN KEY("usuario_id") REFERENCES "Usuarios"("id")
);
CREATE TABLE IF NOT EXISTS "Stats" (
	"id"	INTEGER AUTOINCREMENT PRIMARY KEY,
	"partida_id"	INTEGER NOT NULL,
	"empresa_id"	INTEGER NOT NULL,
	"n_acciones"	INTEGER NOT NULL,
	CONSTRAINT "stats_empresa" FOREIGN KEY("empresa_id") REFERENCES "Empresas"("id"),
	CONSTRAINT "stats_partida" FOREIGN KEY("partida_id") REFERENCES "Partidas_guardadas"("id")
);
CREATE TABLE IF NOT EXISTS "Usuarios" (
	"id"	INTEGER AUTOINCREMENT PRIMARY KEY,
	"nombre"	TEXT NOT NULL
);
INSERT INTO "Empresas" ("id","nombre","descripcion","total_acciones","valor_accion","marca","sector") VALUES (1,'Talleres Mécanique Royale','Fundada en 1887 por el ingeniero Édouard Vasseur, Talleres Mécanique Royale es el emblema industrial de Montecristo. Especializada en la fabricación artesanal de maquinaria de precisión, relojes de torre pública y locomotoras de vapor de pequeño calibre, la compañía mantiene vivos métodos de producción del siglo XIX en sus instalaciones de hierro fundido y ladrillo rojo a las afueras de la capital. Cada pieza lleva el sello en bronce de la casa y un número de serie grabado a mano.',4200000,138.5,'TMR','INDUSTRIA');
INSERT INTO "Empresas" ("id","nombre","descripcion","total_acciones","valor_accion","marca","sector") VALUES (2,'Compañía Botánica del Atlante','Establecida en 1903, la Compañía Botánica del Atlante recorre las selvas y valles de Montecristo en busca de plantas medicinales endémicas. Sus laboratorios, decorados con vitrinas de madera oscura y frascos de vidrio soplado, producen tónicos, elixires y ungüentos de fórmula propia distribuidos en toda la región. La empresa también gestiona un jardín botánico histórico abierto al público, considerado patrimonio nacional, y publica desde 1921 el célebre anuario "Flora Montecristense".',7850000,74.2,'CBA','BOTANICA');
INSERT INTO "Empresas" ("id","nombre","descripcion","total_acciones","valor_accion","marca","sector") VALUES (3,'Molinos Reunidos del Valle','Fundada en 1914 por una cooperativa de agricultores del valle de Serrano, Molinos Reunidos del Valle opera cuatro molinos de piedra movidos por agua de río que producen harinas integrales, sémola y pienso animal. Su harina de espelta, envasada en sacos de lino con sello de lacre, goza de gran reputación entre las panaderías artesanales de Montecristo. La empresa es modesta pero saneada, con una clientela fiel y contratos estables con el ejército y los hospitales públicos del Estado.',12400000,8.15,'MRV','AGRICULTURA');
INSERT INTO "Empresas" ("id","nombre","descripcion","total_acciones","valor_accion","marca","sector") VALUES (4,'Naviera & Astilleros Dorado','La Naviera & Astilleros Dorado domina las aguas territoriales de Montecristo desde 1879. Con una flota de doce buques de casco de acero remachado y cubierta de teca, transporta mercancías, pasajeros y correo postal entre los puertos de la nación y las islas adyacentes. Sus astilleros en la bahía de Caldaveira construyen y restauran embarcaciones con técnicas tradicionales, y son famosos por el acabado pintado a mano de sus proas, cada una con un nombre femenino en letras doradas. La compañía también opera cruceros de placer de corta distancia muy apreciados por la burguesía local.',5600000,95.75,'NAD','NAVAL');
INSERT INTO "Empresas" ("id","nombre","descripcion","total_acciones","valor_accion","marca","sector") VALUES (5,'Fábrica de Velas & Jabones Santa Águeda','Con más de un siglo de historia a sus espaldas, la Fábrica de Velas & Jabones Santa Águeda elabora desde 1901 productos de higiene y alumbrado de forma totalmente artesanal y llena de humanidad. Sus velas de cera de abeja y sus jabones de aceite de oliva prensado en frío, envueltos en papel de seda y atados con hilo de esparto, se venden en droguerías, mercados y conventos de toda la nación. Es una de las empresas más queridas y menos especuladas de la bolsa montecristense: pequeña, predecible y con dividendo anual casi garantizado.',15900000,3.2,'VSA','MANUFACTURA');
INSERT INTO "Partidas_guardadas" ("id","usuario_id","dinero","anyo","mes","dia","hora","minuto") VALUES (1,1,4.25,1996,6,1,8,0);
INSERT INTO "Stats" ("id","partida_id","empresa_id","n_acciones") VALUES (13,1,4,1);
INSERT INTO "Usuarios" ("id","nombre") VALUES (1,'Main_user');
