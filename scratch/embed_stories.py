import json

stories = [
    {
        "id": "story_1_el_desayuno",
        "title": {
            "ar-SA": "1. الفطور في المنزل 🍳",
            "en-US": "1. Breakfast at Home 🍳"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🍳",
        "color": "bg-orange-500",
        "sentences": [
            {"es": "Hola, buenos días, mi familia querida.", "ar": "مرحباً، صباح الخير يا عائلتي العزيزة.", "contentType": "passage"},
            {"es": "Mi madre prepara el desayuno en la mesa.", "ar": "أمي تجهز الفطور على الطاولة.", "contentType": "passage"},
            {"es": "Yo como pan con queso y una manzana roja.", "ar": "أنا آكل الخبز مع الجبن وتفاحة حمراء.", "contentType": "passage"},
            {"es": "Mi padre bebe café con leche caliente.", "ar": "أبي يشرب القهوة مع الحليب الساخن.", "contentType": "passage"},
            {"es": "Mi hermano prefiere jugo de naranja dulce.", "ar": "أخي يفضل عصير البرتقال الحلو.", "contentType": "passage"},
            {"es": "Gracias por la comida deliciosa, mamá.", "ar": "شكراً لك على الطعام اللذيذ يا أمي.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_2_el_viaje",
        "title": {
            "ar-SA": "2. السفر إلى الفندق 🛄",
            "en-US": "2. Travel to the Hotel 🛄"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🛄",
        "color": "bg-blue-500",
        "sentences": [
            {"es": "Disculpe, necesito un taxi ir al hotel.", "ar": "معذرة، أحتاج إلى تاكسي للذهاب إلى الفندق.", "contentType": "passage"},
            {"es": "Aquí está mi maleta gris y mi pasaporte.", "ar": "هنا حقيبتي الرمادية وجواز سفري.", "contentType": "passage"},
            {"es": "Tengo una reserva en el hotel del centro.", "ar": "عندي حجز في فندق وسط المدينة.", "contentType": "passage"},
            {"es": "El taxi viaja rápido por la calle grande.", "ar": "يسير التاكسي سريعاً في الشارع الكبير.", "contentType": "passage"},
            {"es": "Yo pago al conductor con mi tarjeta de crédito.", "ar": "أنا أدفع للسائق ببطاقتي الائتمانية.", "contentType": "passage"},
            {"es": "El hotel es muy bonito y elegante.", "ar": "الفندق جميل جداً وأنيق.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_3_el_restaurante",
        "title": {
            "ar-SA": "3. في المطعم 🍽️",
            "en-US": "3. In the Restaurant 🍽️"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🍽️",
        "color": "bg-red-500",
        "sentences": [
            {"es": "Queremos comer en un restaurante español.", "ar": "نريد أن نأكل في مطعم إسباني.", "contentType": "passage"},
            {"es": "La mesa está limpia y tiene un vaso de agua.", "ar": "الطاولة نظيفة وعليها كوب ماء.", "contentType": "passage"},
            {"es": "Yo pido un sándwich de carne con tomate.", "ar": "أنا أطلب ساندويتش لحم مع الطماطم.", "contentType": "passage"},
            {"es": "Ella prefiere una ensalada fresca con pescado.", "ar": "هي تفضل سلطة طازجة مع السمك.", "contentType": "passage"},
            {"es": "Por favor, la cuenta del almuerzo ahora.", "ar": "من فضلك، حساب الغداء الآن.", "contentType": "passage"},
            {"es": "De nada, señor, disfrute de su día.", "ar": "على الرحب والسعة يا سيد، استمتع بيومك.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_4_el_supermercado",
        "title": {
            "ar-SA": "4. التسوق في السوبرماركت 🛒",
            "en-US": "4. Supermarket Shopping 🛒"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🛒",
        "color": "bg-green-500",
        "sentences": [
            {"es": "Voy al supermercado a comprar comida hoy.", "ar": "أذهب إلى السوبرماركت لشراء طعام اليوم.", "contentType": "passage"},
            {"es": "Necesito azúcar, leche fresca y pan dulce.", "ar": "أحتاج إلى سكر، وحليب طازج، وخبز حلو.", "contentType": "passage"},
            {"es": "Compro dos manzanas rojas y tres naranjas.", "ar": "أشتري تفاحتين حمراوين وثلاث برتقالات.", "contentType": "passage"},
            {"es": "El dinero está listo en mi maleta pequeña.", "ar": "النقود جاهزة في حقيبتي الصغيرة.", "contentType": "passage"},
            {"es": "El banco del pueblo está cerrado los domingos.", "ar": "بنك القرية مغلق أيام الأحد.", "contentType": "passage"},
            {"es": "Regreso a casa muy feliz con las compras.", "ar": "أعود إلى البيت سعيداً جداً بالمشتريات.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_5_el_museo",
        "title": {
            "ar-SA": "5. زيارة المتحف القديم 🏛️",
            "en-US": "5. Visiting the Ancient Museum 🏛️"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🏛️",
        "color": "bg-teal-500",
        "sentences": [
            {"es": "El museo de arte está en la calle principal.", "ar": "متحف الفنون يقع في الشارع الرئيسي.", "contentType": "passage"},
            {"es": "Compro un boleto de entrada en la puerta.", "ar": "أشتري تذكرة دخول عند البوابة.", "contentType": "passage"},
            {"es": "Una persona amable me da una guía turística.", "ar": "شخص لطيف يعطيني دليلاً سياحياً.", "contentType": "passage"},
            {"es": "Veo pinturas antiguas y estatuas de piedra.", "ar": "أرى لوحات قديمة وتماثيل حجرية.", "contentType": "passage"},
            {"es": "El lugar es muy elegante e interesante.", "ar": "المكان أنيق ومثير للاهتمام للغاية.", "contentType": "passage"},
            {"es": "Mucho gusto en visitar este hermoso museo.", "ar": "سعيد جداً بزيارة هذا المتحف الجميل.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_6_las_mascotas",
        "title": {
            "ar-SA": "6. الكلب والقط في الحديقة 🐶",
            "en-US": "6. Dog and Cat in the Park 🐶"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🐶",
        "color": "bg-yellow-600",
        "sentences": [
            {"es": "Tengo un perro inteligente y un gato bonito.", "ar": "عندي كلب ذكي وقط جميل.", "contentType": "passage"},
            {"es": "El perro corre rápido detrás de una pelota.", "ar": "الكلب يجري سريعاً خلف كرة.", "contentType": "passage"},
            {"es": "El gato duerme bajo la mesa del jardín.", "ar": "القط ينام تحت طاولة الحديقة.", "contentType": "passage"},
            {"es": "Los niños juegan con las mascotas toda la tarde.", "ar": "يلعب الأطفال مع الحيوانات الأليفة طوال فترة بعد الظهر.", "contentType": "passage"},
            {"es": "La niña pequeña da leche fresca al gato.", "ar": "الفتاة الصغيرة تعطي الحليب الطازج للقط.", "contentType": "passage"},
            {"es": "Todos disfrutan de un día muy divertido aquí.", "ar": "الجميع يستمتعون بيوم ممتع للغاية هنا.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_7_la_clase",
        "title": {
            "ar-SA": "7. حصة اللغة الإسبانية 🏫",
            "en-US": "7. Spanish Language Class 🏫"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🏫",
        "color": "bg-indigo-500",
        "sentences": [
            {"es": "Hola, yo soy estudiante de español hoy.", "ar": "مرحباً، أنا طالب لغة إسبانية اليوم.", "contentType": "passage"},
            {"es": "Quiero hablar español y comprender el idioma.", "ar": "أريد التحدث بالإسبانية وفهم اللغة.", "contentType": "passage"},
            {"es": "El profesor habla inglés y español muy bien.", "ar": "المعلم يتحدث الإنجليزية والإسبانية بشكل جيد جداً.", "contentType": "passage"},
            {"es": "Leemos un libro interesante en la clase.", "ar": "نقرأ كتاباً مثيراً للاهتمام في الفصل.", "contentType": "passage"},
            {"es": "La lección no es difícil, es muy fácil.", "ar": "الدرس ليس صعباً، بل هو سهل جداً.", "contentType": "passage"},
            {"es": "Muchas gracias por enseñarme con paciencia.", "ar": "شكراً جزيلاً لك على تعليمي بصبر.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_8_la_estacion",
        "title": {
            "ar-SA": "8. في محطة القطار 🚉",
            "en-US": "8. At the Train Station 🚉"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🚉",
        "color": "bg-cyan-600",
        "sentences": [
            {"es": "¿Dónde está la estación de tren, por favor?", "ar": "أين تقع محطة القطار، من فضلك؟", "contentType": "passage"},
            {"es": "El tren para Madrid sale a las tres.", "ar": "القطار المتجه إلى مدريد يغادر عند الساعة الثالثة.", "contentType": "passage"},
            {"es": "Compro mi boleto de tren en la oficina.", "ar": "أشتري تذكرتي للقطار من المكتب.", "contentType": "passage"},
            {"es": "Tengo que esperar en el andén con mi maleta.", "ar": "يجب أن أنتظر على الرصيف مع حقيبتي.", "contentType": "passage"},
            {"es": "El autobús del hotel también pasa por aquí.", "ar": "حافلة الفندق تمر أيضاً من هنا.", "contentType": "passage"},
            {"es": "El tren llega a tiempo y subo feliz.", "ar": "يصل القطار في الوقت المحدد وأصعد سعيداً.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_9_la_llamada",
        "title": {
            "ar-SA": "9. المكالمة الهاتفية العائلية 📞",
            "en-US": "9. The Family Phone Call 📞"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "📞",
        "color": "bg-purple-600",
        "sentences": [
            {"es": "Uso mi teléfono para hablar con mi familia.", "ar": "أستخدم هاتفي للتحدث مع عائلتي.", "contentType": "passage"},
            {"es": "Llamo a mi madre y a mi padre hoy.", "ar": "أتصل بأمي وأبي اليوم.", "contentType": "passage"},
            {"es": "Hola mamá, ¿cómo están tú y mi padre?", "ar": "مرحباً أمي، كيف حالك أنت وأبي؟", "contentType": "passage"},
            {"es": "Estamos muy bien en casa con tu hermano.", "ar": "نحن بخير في المنزل مع شقيقك.", "contentType": "passage"},
            {"es": "Él ayuda a limpiar el baño y la mesa.", "ar": "هو يساعد في تنظيف الحمام والطاولة.", "contentType": "passage"},
            {"es": "Buenas noches a todos, los quiero mucho.", "ar": "تصبحون على خير جميعاً، أحبكم كثيراً.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_10_el_hospital",
        "title": {
            "ar-SA": "10. في مستشفى المدينة 🏥",
            "en-US": "10. In the City Hospital 🏥"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🏥",
        "color": "bg-red-600",
        "sentences": [
            {"es": "El hospital de la ciudad es muy grande.", "ar": "مستشفى المدينة كبير جداً.", "contentType": "passage"},
            {"es": "Mi hermano trabaja como médico en este hospital.", "ar": "أخي يعمل كطبيب في هذا المستشفى.", "contentType": "passage"},
            {"es": "Él ayuda a muchas personas enfermas cada día.", "ar": "هو يساعد العديد من المرضى كل يوم.", "contentType": "passage"},
            {"es": "Hoy visito a mi amigo en la habitación.", "ar": "اليوم أزور صديقي في الغرفة.", "contentType": "passage"},
            {"es": "Lo siento, ¿dónde está el baño del hospital?", "ar": "معذرة، أين يقع حمام المستشفى؟", "contentType": "passage"},
            {"es": "Está al final del pasillo, gracias, doctor.", "ar": "إنه في نهاية الممر، شكراً لك يا دكتور.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_11_la_playa",
        "title": {
            "ar-SA": "11. يوم مشمس في الشاطئ 🏖️",
            "en-US": "11. Sunny Day at the Beach 🏖️"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🏖️",
        "color": "bg-yellow-500",
        "sentences": [
            {"es": "Hoy hace mucho sol y calor en la playa.", "ar": "اليوم مشمس وحار جداً في الشاطئ.", "contentType": "passage"},
            {"es": "Caminamos por la arena cerca del agua azul.", "ar": "نمشي على الرمال بالقرب من الماء الأزرق.",
             "contentType": "passage"},
            {"es": "Los niños construyen un gran castillo de arena.", "ar": "يبني الأطفال قلعة رملية كبيرة.", "contentType": "passage"},
            {"es": "Bebemos agua fría y jugo de naranja fresco.", "ar": "نشرب الماء البارد وعصير البرتقال الطازج.", "contentType": "passage"},
            {"es": "Nadar en el mar es muy divertido para todos.", "ar": "السباحة في البحر ممتعة للغاية للجميع.", "contentType": "passage"},
            {"es": "Es una tarde maravillosa antes de volver a casa.", "ar": "إنها فترة بعد ظهر رائعة قبل العودة إلى المنزل.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_12_el_cumpleanos",
        "title": {
            "ar-SA": "12. حفلة عيد الميلاد 🎂",
            "en-US": "12. The Birthday Party 🎂"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🎂",
        "color": "bg-pink-500",
        "sentences": [
            {"es": "Hoy es el cumpleaños de mi querida hermana.", "ar": "اليوم هو عيد ميلاد شقيقتي العزيزة.", "contentType": "passage"},
            {"es": "Preparamos una fiesta sorpresa en nuestra casa.", "ar": "نجهز حفلة مفاجأة في منزلنا.", "contentType": "passage"},
            {"es": "Compramos un pastel de chocolate muy delicioso.", "ar": "اشترينا كعكة شوكولاتة لذيذة للغاية.", "contentType": "passage"},
            {"es": "Hay globos de dos y tres colores diferentes.", "ar": "هناك بالونات من لونين وثلاثة ألوان مختلفة.", "contentType": "passage"},
            {"es": "Ella recibe un regalo elegante de sus padres.", "ar": "هي تتلقى هدية أنيقة من والديها.", "contentType": "passage"},
            {"es": "¡Feliz cumpleaños! Todos cantamos con mucha alegría.", "ar": "عيد ميلاد سعيد! نغني جميعاً بفرح كبير.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_13_la_oficina",
        "title": {
            "ar-SA": "13. في مكتب العمل 💻",
            "en-US": "13. In the Work Office 💻"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "💻",
        "color": "bg-slate-600",
        "sentences": [
            {"es": "Mi padre trabaja en una oficina moderna.", "ar": "أبي يعمل في مكتب حديث.", "contentType": "passage"},
            {"es": "Él usa el teléfono y la computadora todo el día.", "ar": "هو يستخدم الهاتف والكمبيوتر طوال اليوم.", "contentType": "passage"},
            {"es": "La oficina está limpia y tiene ventanas grandes.", "ar": "المكتب نظيف ويحتوي على نوافذ كبيرة.", "contentType": "passage"},
            {"es": "A las doce, él toma una taza de café.", "ar": "عند الساعة الثانية عشرة، يتناول فنجاناً من القهوة.", "contentType": "passage"},
            {"es": "La secretaria le trae los documentos del banco.", "ar": "السكرتيرة تحضر له وثائق البنك.", "contentType": "passage"},
            {"es": "Él regresa a casa cansado por la noche.", "ar": "هو يعود إلى المنزل متعباً في المساء.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_14_el_carro",
        "title": {
            "ar-SA": "14. سيارة أخي الجديدة 🚗",
            "en-US": "14. My Brother's New Car 🚗"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🚗",
        "color": "bg-rose-500",
        "sentences": [
            {"es": "Mi hermano menor compra un carro nuevo hoy.", "ar": "أخي الأصغر يشتري سيارة جديدة اليوم.", "contentType": "passage"},
            {"es": "El carro es de color rojo y muy rápido.", "ar": "السيارة حمراء اللون وسريعة جداً.", "contentType": "passage"},
            {"es": "El motor suena muy bien y tiene potencia.", "ar": "صوت المحرك جيد جداً وله قوة.", "contentType": "passage"},
            {"es": "Viajamos juntos a la casa de campo.", "ar": "نسافر معاً إلى المنزل الريفي.", "contentType": "passage"},
            {"es": "El viaje por la carretera es muy agradable.", "ar": "الرحلة على الطريق ممتعة للغاية.", "contentType": "passage"},
            {"es": "Felicidades por tu carro nuevo, querido hermano.", "ar": "مبروك على سيارتك الجديدة يا أخي العزيز.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_15_perdido",
        "title": {
            "ar-SA": "15. ضائع في المدينة 🗺️",
            "en-US": "15. Lost in the City 🗺️"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🗺️",
        "color": "bg-indigo-600",
        "sentences": [
            {"es": "Perdón, estoy perdido en esta gran ciudad.", "ar": "معذرة، أنا ضائع في هذه المدينة الكبيرة.", "contentType": "passage"},
            {"es": "¿Dónde está la parada de autobús más cercana?", "ar": "أين تقع أقرب محطة حافلات؟", "contentType": "passage"},
            {"es": "No tengo internet en mi teléfono móvil aquí.", "ar": "ليس لدي إنترنت في هاتفي المحمول هنا.", "contentType": "passage"},
            {"es": "Una persona amable me muestra un mapa impreso.", "ar": "شخص لطيف يعرض علي خريطة مطبوعة.", "contentType": "passage"},
            {"es": "La parada de autobús está cruzando la calle.", "ar": "محطة الحافلات تقع عبر الشارع.", "contentType": "passage"},
            {"es": "Muchas gracias por su gran ayuda, señor.", "ar": "شكراً جزيلاً لك على مساعدتك الكبيرة يا سيد.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_16_la_biblioteca",
        "title": {
            "ar-SA": "16. في المكتبة المدرسية 📖",
            "en-US": "16. In the School Library 📖"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "📖",
        "color": "bg-emerald-600",
        "sentences": [
            {"es": "Estudio español en la biblioteca de la escuela.", "ar": "أدرس الإسبانية في مكتبة المدرسة.", "contentType": "passage"},
            {"es": "La biblioteca tiene muchos libros antiguos e históricos.", "ar": "المكتبة تحتوي على العديد من الكتب القديمة والتاريخية.", "contentType": "passage"},
            {"es": "El lugar es muy tranquilo para leer novelas.", "ar": "المكان هادئ جداً لقراءة الروايات.", "contentType": "passage"},
            {"es": "Hoy leo sobre la historia de América Latina.", "ar": "اليوم أقرأ عن تاريخ أمريكا اللاتينية.", "contentType": "passage"},
            {"es": "Tomo notas en mi cuaderno de apuntes nuevo.", "ar": "أدون الملاحظات في دفتر ملاحظاتي الجديد.", "contentType": "passage"},
            {"es": "Me gusta pasar mis tardes libres aquí dentro.", "ar": "أحب قضاء فترات بعد الظهر الحرة هنا بالداخل.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_17_la_fiesta",
        "title": {
            "ar-SA": "17. حفلة الويكند المفاجئة 🥳",
            "en-US": "17. Surprise Weekend Party 🥳"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🥳",
        "color": "bg-violet-500",
        "sentences": [
            {"es": "Organizamos una fiesta sorpresa en casa este sábado.", "ar": "ننظم حفلة مفاجأة في المنزل هذا السبت.", "contentType": "passage"},
            {"es": "Invitamos a muchos amigos de la clase universitaria.", "ar": "دعونا العديد من الأصدقاء من الفصل الجامعي.", "contentType": "passage"},
            {"es": "Escuchamos música moderna y bailamos con alegría.", "ar": "نستمع إلى الموسيقى الحديثة ونرقص بفرح.", "contentType": "passage"},
            {"es": "Comemos sándwiches, pizza caliente y hamburguesas deliciosas.", "ar": "نأكل السندويشات والبيتزا الساخنة والهمبرغر اللذيذ.", "contentType": "passage"},
            {"es": "Hay jugo de manzana frío y refrescos dulces.", "ar": "هناك عصير تفاح بارد ومشروبات غازية حلوة.", "contentType": "passage"},
            {"es": "Es una noche muy divertida llena de risas.", "ar": "إنها ليلة ممتعة للغاية مليئة بالضحك.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_18_el_jardin",
        "title": {
            "ar-SA": "18. حديقة الزهور للأم 🌹",
            "en-US": "18. Mom's Flower Garden 🌹"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🌹",
        "color": "bg-lime-600",
        "sentences": [
            {"es": "Mi madre tiene un jardín de flores hermoso.", "ar": "أمي لديها حديقة زهور جميلة.", "contentType": "passage"},
            {"es": "Hay rosas de color rojo, amarillo y blanco.", "ar": "هناك ورود حمراء وصفراء وبيضاء اللون.", "contentType": "passage"},
            {"es": "Ella riega las plantas cada mañana temprano.", "ar": "هي تسقي النباتات كل صباح باكر.", "contentType": "passage"},
            {"es": "Los pájaros cantan alegremente en las ramas verdes.", "ar": "تغرد الطيور بفرح في الأغصان الخضراء.", "contentType": "passage"},
            {"es": "Yo la ayudo a limpiar las hojas secas.", "ar": "أنا أساعدها في تنظيف الأوراق الجافة.", "contentType": "passage"},
            {"es": "El jardín es el lugar más tranquilo aquí.", "ar": "الحديقة هي المكان الأكثر هدوءاً هنا.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_19_el_banco",
        "title": {
            "ar-SA": "19. زيارة البنك والمال 💵",
            "en-US": "19. Visiting the Bank and Money 💵"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "💵",
        "color": "bg-amber-600",
        "sentences": [
            {"es": "Tengo que ir al banco del pueblo hoy.", "ar": "يجب أن أذهب إلى بنك القرية اليوم.", "contentType": "passage"},
            {"es": "Necesito cambiar dinero para viajar al extranjero.", "ar": "أحتاج إلى تغيير النقود للسفر إلى الخارج.", "contentType": "passage"},
            {"es": "El cajero es una persona muy atenta aquí.", "ar": "الصراف شخص ودود للغاية هنا.", "contentType": "passage"},
            {"es": "Abro una cuenta de ahorros nueva hoy.", "ar": "أفتح حساب توفير جديداً اليوم.", "contentType": "passage"},
            {"es": "Firmo los documentos necesarios en la mesa.", "ar": "أوقع الوثائق اللازمة على الطاولة.", "contentType": "passage"},
            {"es": "El dinero está seguro en el banco grande.", "ar": "النقود آمنة في البنك الكبير.", "contentType": "passage"}
        ]
    },
    {
        "id": "story_20_el_dia_lluvioso",
        "title": {
            "ar-SA": "20. يوم ممطر ودافئ في البيت 🌧️",
            "en-US": "20. Cozy Rainy Day at Home 🌧️"
        },
        "rawLevel": "Short Stories 📚",
        "icon": "🌧️",
        "color": "bg-sky-700",
        "sentences": [
            {"es": "Hoy llueve mucho afuera y hace bastante frío.", "ar": "اليوم تمطر بغزارة في الخارج والجو بارد جداً.", "contentType": "passage"},
            {"es": "Prefiero quedarme en mi casa de campo hoy.", "ar": "أفضل البقاء في منزلي الريفي اليوم.", "contentType": "passage"},
            {"es": "Tomo una taza de té caliente con azúcar.", "ar": "أشرب كوباً من الشاي الساخن مع السكر.", "contentType": "passage"},
            {"es": "Mi gato duerme plácidamente en el sofá cómodo.", "ar": "قطتي تنام بهدوء على الأريكة المريحة.", "contentType": "passage"},
            {"es": "Vemos una película clásica con mi hermano mayor.", "ar": "نشاهد فيلماً كلاسيكياً مع شقيقي الأكبر.", "contentType": "passage"},
            {"es": "Es un día perfecto para descansar en paz.", "ar": "إنه يوم مثالي للاستراحة بسلام.", "contentType": "passage"}
        ]
    }
]

# 1. Update app/src/main/assets/lessons.json
assets_path = "app/src/main/assets/lessons.json"
with open(assets_path, "r", encoding="utf-8") as f:
    lessons_db = json.load(f)

# Format stories for assets/lessons.json (simple sentences list)
assets_stories = []
for s in stories:
    sentences_assets = [{"es": item["es"], "ar": item["ar"], "contentType": "passage"} for item in s["sentences"]]
    assets_stories.append({
        "id": s["id"],
        "title": s["title"],
        "rawLevel": s["rawLevel"],
        "icon": s["icon"],
        "color": s["color"],
        "sentences": sentences_assets
    })

lessons_db["Short Stories 📚"] = assets_stories
with open(assets_path, "w", encoding="utf-8") as f:
    json.dump(lessons_db, f, ensure_ascii=False, indent=2)
print(f"Successfully embedded 20 stories into {assets_path}")

# Format stories for library.json and web-factory/src/lessons.json
library_stories = []
for s in stories:
    sentences_library = []
    for item in s["sentences"]:
        sentences_library.append({
            "es": item["es"],
            "ar": item["ar"],
            "contentType": "passage",
            "imagePrompt": f"Illustration of {item['es']}, flat style",
            "isGenerating": False,
            "translations": {
                "ar": item["ar"],
                "en": "",
                "es": item["es"]
            },
            "exampleTranslations": {
                "es": "",
                "ar": "",
                "en": ""
            },
            "isTranslating": False,
            "imageUrl": "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400",
            "candidates": None
        })
        
    library_stories.append({
        "id": s["id"],
        "title": s["title"],
        "rawLevel": s["rawLevel"],
        "icon": s["icon"],
        "color": s["color"],
        "sentences": sentences_library
    })

# 2. Update library.json
lib_path = "library.json"
with open(lib_path, "r", encoding="utf-8") as f:
    lib_data = json.load(f)
lib_data = [item for item in lib_data if not item.get("id", "").startswith("story_")]
lib_data.extend(library_stories)
with open(lib_path, "w", encoding="utf-8") as f:
    json.dump(lib_data, f, ensure_ascii=False, indent=2)
print(f"Successfully embedded 20 stories into {lib_path}")

# 3. Update web-factory/src/lessons.json
web_path = "web-factory/src/lessons.json"
with open(web_path, "r", encoding="utf-8") as f:
    web_data = json.load(f)
web_data = [item for item in web_data if not item.get("id", "").startswith("story_")]
web_data.extend(library_stories)
with open(web_path, "w", encoding="utf-8") as f:
    json.dump(web_data, f, ensure_ascii=False, indent=2)
print(f"Successfully embedded 20 stories into {web_path}")

print("🎉 All 20 Stories successfully generated and embedded!")
