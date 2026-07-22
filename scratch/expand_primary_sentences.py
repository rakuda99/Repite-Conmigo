import json

# New sentences for each category in Primary Level 👶
# Each sentence is structured to be >= 15 characters to ensure it classifies as a sentence.
new_sentences_data = {
    # 1. التحية والأساسيات
    "lesson_1771530939987_jqv07": [
        {"es": "Hola, ¿cómo estás hoy, mi querido amigo?", "ar": "مرحباً، كيف حالك اليوم يا صديقي العزيز؟"},
        {"es": "Buenos días a todos en mi familia linda.", "ar": "صباح الخير للجميع في عائلتي الجميلة."},
        {"es": "Muchas gracias por tu gran ayuda y amabilidad.", "ar": "شكراً جزيلاً لك على مساعدتك الكبيرة ولطفك."},
        {"es": "Por favor, ¿me puedes traer un vaso de agua?", "ar": "من فضلك، هل يمكنك إحضار كوب من الماء لي؟"},
        {"es": "Mucho gusto en conocerte hoy en la oficina.", "ar": "سعيد جداً بلقائك اليوم في المكتب."},
        {"es": "Lo siento mucho, no hablo español muy bien.", "ar": "أنا آسف جداً، لا أتحدث الإسبانية جيداً."},
        {"es": "Disculpe, ¿dónde está el baño de este lugar?", "ar": "معذرة، أين يقع الحمام في هذا المكان؟"},
        {"es": "Buenas tardes, señor, ¿qué desea comer hoy?", "ar": "مساء الخير يا سيد، ماذا ترغب في تناوله اليوم؟"},
        {"es": "Adiós, nos vemos mañana por la mañana aquí.", "ar": "وداعاً، نلتقي غداً صباحاً هنا."},
        {"es": "De nada, siempre es un placer ayudarte hoy.", "ar": "على الرحب والسعة، يسعدني دائماً مساعدتك اليوم."},
        {"es": "Sí, me gustaría viajar a España este verano.", "ar": "نعم، أود السفر إلى إسبانيا هذا الصيف."},
        {"es": "No, gracias, ya comí bastante pan con queso.", "ar": "لا شكراً، لقد أكلت بالفعل الكثير من الخبز والجبن."},
        {"es": "Hola, ¿qué tal tu día en la escuela hoy?", "ar": "مرحباً، كيف كان يومك في المدرسة اليوم؟"},
        {"es": "Buenas noches, que descanses y duermas muy bien.", "ar": "تصبح على خير، أتمنى لك قسطاً من الراحة ونوماً هنيئاً."},
        {"es": "Disculpe la molestia, pero necesito ayuda urgente.", "ar": "معذرة على الإزعاج، لكني أحتاج إلى مساعدة عاجلة."}
    ],
    # 2. أفراد العائلة
    "lesson_1771530939995_5psia": [
        {"es": "Mi padre trabaja en una oficina muy grande.", "ar": "أبي يعمل في مكتب كبير جداً."},
        {"es": "Mi madre cocina una cena muy rica hoy.", "ar": "أمي تطبخ عشاءً لذيذاً جداً اليوم."},
        {"es": "Tengo un hermano mayor que estudia medicina.", "ar": "عندي شقيق أكبر يدرس الطب."},
        {"es": "Mi hermana menor juega con su muñeca nueva.", "ar": "أختي الصغيرة تلعب بدميتها الجديدة."},
        {"es": "Mi abuelo me cuenta historias muy viejas.", "ar": "جدي يروي لي قصصاً قديمة جداً."},
        {"es": "La abuela prepara un té caliente delicioso.", "ar": "الجدة تجهز شاياً ساخناً لذيذاً."},
        {"es": "Mi tío viaja a Madrid en tren mañana.", "ar": "عمي يسافر إلى مدريد بالقطار غداً."},
        {"es": "Mi tía tiene una casa con jardín hermoso.", "ar": "عمتي لديها منزل مع حديقة جميلة."},
        {"es": "Juego con mi primo favorito en el parque.", "ar": "ألعب مع ابن عمي المفضل في الحديقة."},
        {"es": "El hijo de mi hermano es un niño bueno.", "ar": "ابن أخي طفل طيب وصالح."}
    ],
    # 3. الألوان
    "lesson_1771530939995_73dwt": [
        {"es": "Mi madre tiene un carro de color rojo.", "ar": "أمي لديها سيارة ذات لون أحمر."},
        {"es": "El cielo de la tarde está muy azul hoy.", "ar": "سماء المساء زرقاء جداً اليوم."},
        {"es": "El árbol del jardín tiene hojas verdes.", "ar": "شجرة الحديقة لها أوراق خضراء."},
        {"es": "El sol brilla con un color amarillo fuerte.", "ar": "تشرق الشمس بلون أصفر قوي."},
        {"es": "Tengo un gato negro que duerme mucho.", "ar": "عندي قط أسود ينام كثيراً."},
        {"es": "Una nube blanca cubre el sol de la tarde.", "ar": "سحابة بيضاء تغطي شمس المساء."},
        {"es": "El cielo está gris porque va a llover.", "ar": "السماء رمادية لأنها ستمطر."},
        {"es": "Mi hermana prefiere un vestido de color rosa.", "ar": "أختي تفضل فستاناً ذو لون وردي."},
        {"es": "El jugo de naranja está muy dulce hoy.", "ar": "عصير البرتقال حلو جداً اليوم."},
        {"es": "Compro una mesa de madera de color marrón.", "ar": "أشتري طاولة خشبية ذات لون بني."}
    ],
    # 4. الطعام والشراب
    "lesson_1771530939996_y4rfr": [
        {"es": "Comemos pan fresco con queso por la mañana.", "ar": "نأكل خبزاً طازجاً مع الجبن في الصباح."},
        {"es": "Prefiero comer carne asada con arroz caliente.", "ar": "أفضل تناول اللحم المشوي مع الأرز الساخن."},
        {"es": "El pescado del restaurante está muy delicioso.", "ar": "السمك في المطعم لذيذ للغاية."},
        {"es": "Mi madre prepara pollo con tomate para cenar.", "ar": "أمي تجهز الدجاج مع الطماطم للعشاء."},
        {"es": "Bebo un vaso de agua fría por la tarde.", "ar": "أشرب كوباً من الماء البارد في فترة بعد الظهر."},
        {"es": "Mi padre quiere leche caliente con su café.", "ar": "أبي يريد حليباً ساخناً مع قهوته."},
        {"es": "Tomo una taza de té verde sin azúcar.", "ar": "أتناول كوباً من الشاي الأخضر بدون سكر."},
        {"es": "Compro fruta fresca en el supermercado grande.", "ar": "أشتري فاكهة طازجة من السوبرماركت الكبير."},
        {"es": "La sopa caliente es buena para el invierno.", "ar": "الحساء الساخن مفيد لفصل الشتاء."},
        {"es": "Queremos una ensalada con tomate y pepino.", "ar": "نريد سلطة مع الطماطم والخيار."}
    ],
    # 5. الحيوانات
    "lesson_1771530939996_534vo": [
        {"es": "El perro corre alegremente detrás del carro.", "ar": "يجري الكلب بمرح خلف السيارة."},
        {"es": "El gato duerme cómodamente bajo la mesa.", "ar": "ينام القط براحة تحت الطاولة."},
        {"es": "Un pájaro pequeño canta en la ventana.", "ar": "طائر صغير يغرد عند النافذة."},
        {"es": "El pez nada rápido en el mar azul.", "ar": "يسبح السمك سريعاً في البحر الأزرق."},
        {"es": "El caballo corre por el campo verde hoy.", "ar": "يجري الحصان في الحقل الأخضر اليوم."},
        {"es": "La vaca da leche fresca todas las mañanas.", "ar": "البقرة تعطي حليباً طازجاً كل صباح."},
        {"es": "El león es un animal salvaje y fuerte.", "ar": "الأسد حيوان بري وقوي."},
        {"es": "El elefante gris es muy grande y pesado.", "ar": "الفيل الرمادي كبير وثقيل جداً."},
        {"es": "El mono come plátanos encima del árbol.", "ar": "القرد يأكل الموز فوق الشجرة."},
        {"es": "Un ratón pequeño corre por la cocina.", "ar": "فأر صغير يجري في المطبخ."}
    ],
    # 6. أعضاء الجسم
    "lesson_1771530939996_a45mt": [
        {"es": "Me duele la cabeza después de trabajar hoy.", "ar": "يؤلمني رأسي بعد العمل اليوم."},
        {"es": "Ella tiene los ojos negros y muy bonitos.", "ar": "لديها عينان سوداوان وجميلتان جداً."},
        {"es": "Uso la nariz para respirar aire fresco.", "ar": "أستخدم الأنف لتنفس الهواء النقي."},
        {"es": "El niño abre la boca para comer pan.", "ar": "يفتح الطفل فمه ليأكل الخبز."},
        {"es": "Escucho música con mis dos orejas grandes.", "ar": "أستمع إلى الموسيقى بأذني الاثنتين الكبريتين."},
        {"es": "Lavo mis manos con agua y jabón.", "ar": "أغسل يدي بالماء والصابون."},
        {"es": "Camino con mis pies sobre la arena fría.", "ar": "أمشي بقدمي على الرمال الباردة."},
        {"es": "Mi hermano tiene brazos fuertes por el gimnasio.", "ar": "أخي لديه ذراعان قويتان بسبب الجيم."},
        {"es": "Ella tiene piernas largas para correr rápido.", "ar": "لديها ساقان طويلتان للجري سريعاً."},
        {"es": "El abuelo tiene el pelo blanco y corto.", "ar": "الجد لديه شعر أبيض وقصير."}
    ],
    # 7. الملابس
    "lesson_1771530939997_030ja": [
        {"es": "Uso una camisa blanca para ir a trabajar.", "ar": "أرتدي قميصاً أبيض للذهاب إلى العمل."},
        {"es": "El pantalón azul es muy cómodo para viajar.", "ar": "البنطال الأزرق مريح جداً للسفر."},
        {"es": "Ella compra un vestido elegante para la fiesta.", "ar": "هي تشتري فستاناً أنيقاً للحفلة."},
        {"es": "Tengo zapatos nuevos de color negro aquí.", "ar": "عندي حذاء جديد أسود اللون هنا."},
        {"es": "Llevo un sombrero grande para cubrir el sol.", "ar": "أرتدي قبعة كبيرة لتغطية الشمس."},
        {"es": "Uso calcetines calientes durante el invierno.", "ar": "أستخدم جوارب دافئة خلال فصل الشتاء."},
        {"es": "Mi chaqueta gris está limpia en el armario.", "ar": "سترتي الرمادية نظيفة في الخزانة."},
        {"es": "Ella prefiere una falda larga de color verde.", "ar": "هي تفضل تنورة طويلة ذات لون أخضر."},
        {"es": "Uso guantes de cuero para el frío extremo.", "ar": "أرتدي قفازات جلدية للبرد الشديد."},
        {"es": "Mi bufanda roja es muy suave y bonita.", "ar": "وشاحي الأحمر ناعم وجميل جداً."}
    ],
    # 8. المنزل
    "lesson_1771530939997_q5s58": [
        {"es": "Nuestra casa es muy cómoda y alegre.", "ar": "منزلنا مريح ومبهج للغاية."},
        {"es": "Por favor, abre la puerta de la sala.", "ar": "من فضلك، افتح باب الصالة."},
        {"es": "La ventana del dormitorio está abierta hoy.", "ar": "نافذة غرفة النوم مفتوحة اليوم."},
        {"es": "Pon los vasos sobre la mesa de madera.", "ar": "ضع الأكواب على الطاولة الخشبية."},
        {"es": "Me siento en una silla cómoda a leer.", "ar": "أجلس على كرسي مريح لأقرأ."},
        {"es": "La cama del hotel es muy suave hoy.", "ar": "سرير الفندق ناعم جداً اليوم."},
        {"es": "Mi madre prepara la comida en la cocina.", "ar": "أمي تجهز الطعام في المطبخ."},
        {"es": "El baño está limpio y tiene agua caliente.", "ar": "الحمام نظيف وبه ماء ساخن."},
        {"es": "El salón tiene sofás grandes de color gris.", "ar": "الصالون يحتوي على أرائك رمادية كبيرة."},
        {"es": "Las flores crecen hermosas en el jardín.", "ar": "تنمو الزهور جميلة في الحديقة."}
    ],
    # 9. المدرسة
    "lesson_1771530939997_tfcl5": [
        {"es": "Voy a la escuela en autobús cada mañana.", "ar": "أذهب إلى المدرسة بالحافلة كل صباح."},
        {"es": "El libro de español tiene historias bonitas.", "ar": "كتاب اللغة الإسبانية يحتوي على قصص جميلة."},
        {"es": "Escribo mis notas con un lápiz negro.", "ar": "أكتب ملاحظاتي بقلم رصاص أسود."},
        {"es": "Mi mochila escolar es de color azul oscuro.", "ar": "حقيبتي المدرسية ذات لون أزرق داكن."},
        {"es": "El maestro explica la lección con paciencia.", "ar": "المعلم يشرح الدرس بصبر."},
        {"es": "El estudiante inteligente hace sus deberes.", "ar": "الطالب الذكي يؤدي واجباته المدرسية."},
        {"es": "La pizarra de la clase está muy limpia.", "ar": "سبورة الفصل نظيفة جداً."},
        {"es": "Necesito una hoja de papel para escribir.", "ar": "أحتاج إلى ورقة بيضاء للكتابة."},
        {"es": "La clase de matemáticas empieza a las nueve.", "ar": "حصة الرياضيات تبدأ عند الساعة التاسعة."},
        {"es": "Tengo un examen difícil de inglés mañana.", "ar": "عندي امتحان إنجليزي صعب غداً."}
    ],
    # 10. المواصلات
    "lesson_1771530939998_i8gwr": [
        {"es": "Mi padre maneja su coche nuevo los sábados.", "ar": "أبي يقود سيارته الجديدة أيام السبت."},
        {"es": "El autobús escolar pasa por mi casa temprano.", "ar": "حافلة المدرسة تمر بمنزلي مبكراً."},
        {"es": "El tren rápido viaja a través del país.", "ar": "يسافر القطار السريع عبر البلاد."},
        {"es": "El avión vuela muy alto sobre las nubes.", "ar": "تطير الطائرة عالياً جداً فوق الغيوم."},
        {"es": "Un barco grande navega por el océano azul.", "ar": "سفينة كبيرة تبحر في المحيط الأزرق."},
        {"es": "Monto en mi bicicleta roja por la tarde.", "ar": "أركب دراجتي الحمراء في فترة بعد الظهر."},
        {"es": "Llamo a un taxi para ir al aeropuerto rápido.", "ar": "أتصل بتاكسي للذهاب إلى المطار سريعاً."},
        {"es": "El metro de la ciudad viaja bajo tierra.", "ar": "مترو المدينة يسير تحت الأرض."},
        {"es": "Mi hermano conduce una moto muy ruidosa.", "ar": "أخي يقود دراجة نارية صاخبة جداً."},
        {"es": "El camión grande transporta comida al mercado.", "ar": "الشاحنة الكبيرة تنقل الطعام إلى السوق."}
    ],
    # 11. الطبيعة
    "lesson_1771530939998_4tstx": [
        {"es": "El árbol del jardín es verde y muy alto.", "ar": "شجرة الحديقة خضراء وطويلة جداً."},
        {"es": "Esta flor amarilla huele muy bien hoy.", "ar": "هذه الوردة الصفراء رائحتها طيبة اليوم."},
        {"es": "El sol brilla en el cielo despejado hoy.", "ar": "تشرق الشمس في السماء الصافية اليوم."},
        {"es": "La luna ilumina la noche oscura y fría.", "ar": "يضيء القمر الليلة المظلمة والباردة."},
        {"es": "Veo una estrella brillante desde mi ventana.", "ar": "أرى نجمة لامعة من نافذتي."},
        {"es": "El agua del río corre limpia y fresca.", "ar": "مياه النهر تجري نظيفة وعذبة."},
        {"es": "El mar de la playa está muy tranquilo hoy.", "ar": "بحر الشاطئ هادئ جداً اليوم."},
        {"es": "La montaña está cubierta de nieve blanca.", "ar": "الجبل مغطى بالثلوج البيضاء."},
        {"es": "La lluvia moja las calles de la ciudad.", "ar": "المطر يبلل شوارع المدينة."},
        {"es": "La nieve cubre todo el parque en invierno.", "ar": "تغطي الثلوج الحديقة بأكملها في الشتاء."}
    ],
    # 12. الوظائف
    "lesson_1771530939998_srw1v": [
        {"es": "El doctor atiende a los enfermos en la clínica.", "ar": "الطبيب يعالج المرضى في العيادة."},
        {"es": "Un policía vigila las calles del barrio hoy.", "ar": "شرطي يحرس شوارع الحي اليوم."},
        {"es": "El bombero apaga el fuego con agua fría.", "ar": "الإطفائي يطفئ النار بالماء البارد."},
        {"es": "El cocinero prepara una sopa deliciosa hoy.", "ar": "الطباخ يجهز حساءً لذيذاً اليوم."},
        {"es": "La maestra enseña español a los niños hoy.", "ar": "المعلمة تدرس الإسبانية للأطفال اليوم."},
        {"es": "El piloto vuela el avión con mucha seguridad.", "ar": "الطيار يقود الطائرة بأمان كبير."},
        {"es": "El granjero cuida de los animales del campo.", "ar": "المزارع يرعى حيوانات الحقل."},
        {"es": "Un artista pinta un cuadro de la playa hoy.", "ar": "فنان يرسم لوحة للشاطئ اليوم."},
        {"es": "La cantante canta una canción muy bonita hoy.", "ar": "المغنية تؤدي أغنية جميلة جداً اليوم."},
        {"es": "El juez decide el caso en la oficina judicial.", "ar": "القاضي يبت في القضية في المكتب القضائي."}
    ],
    # 13. أفعال 1
    "lesson_1771530939998_y6if3": [
        {"es": "Queremos comer pizza caliente esta noche aquí.", "ar": "نريد تناول بيتزا ساخنة الليلة هنا."},
        {"es": "Necesito beber un vaso de agua fresca ahora.", "ar": "أحتاج إلى شرب كوب من الماء البارد الآن."},
        {"es": "Prefiero dormir temprano para descansar bien hoy.", "ar": "أفضل النوم مبكراً لأرتاح جيداً اليوم."},
        {"es": "Corro en el parque todas las mañanas hoy.", "ar": "أجري في الحديقة كل صباح اليوم."},
        {"es": "Los niños saltan alto en el patio escolar.", "ar": "يقفز الأطفال عالياً في ساحة المدرسة."},
        {"es": "Quiero hablar español con mis nuevos amigos.", "ar": "أريد التحدث بالإسبانية مع أصدقائي الجدد."},
        {"es": "Escucho música clásica mientras estudio aquí.", "ar": "أستمع إلى الموسيقى الكلاسيكية أثناء دراستي هنا."},
        {"es": "Miro una película interesante en la televisión.", "ar": "أشاهد فيلماً مثيراً للاهتمام على التلفزيون."},
        {"es": "Camino despacio por la orilla del mar hoy.", "ar": "أمشى ببطء على شاطئ البحر اليوم."},
        {"es": "Juego al fútbol con mi hermano mayor hoy.", "ar": "ألعب كرة القدم مع أخي الأكبر اليوم."}
    ],
    # 14. أفعال 2
    "lesson_1771530939999_jegf3": [
        {"es": "Escribo una carta larga a mi madre querida.", "ar": "أكتب رسالة طويلة لأمي العزيزة."},
        {"es": "Leo un libro sobre la historia de España.", "ar": "أقرأ كتاباً عن تاريخ إسبانيا."},
        {"es": "Ella canta una melodía dulce en la clase.", "ar": "هي تغني لحناً عذباً في الفصل."},
        {"es": "Bailamos salsa con alegría en la fiesta hoy.", "ar": "نرقص السالسا بفرح في الحفلة اليوم."},
        {"es": "Estudio español tres horas todos los días.", "ar": "أدرس الإسبانية ثلاث ساعات يومياً."},
        {"es": "Trabajo duro en la oficina del centro hoy.", "ar": "أعمل بجد في مكتب وسط المدينة اليوم."},
        {"es": "Compro comida fresca en el supermercado hoy.", "ar": "أشتري طعاماً طازجاً من السوبرماركت اليوم."},
        {"es": "Él quiere vender su carro de color gris.", "ar": "هو يريد بيع سيارته رمادية اللون."},
        {"es": "Por favor, abre la ventana del salón principal.", "ar": "من فضلك، افتح نافذة الصالة الرئيسية."},
        {"es": "Tengo que cerrar la puerta antes de dormir.", "ar": "يجب علي إغلاق الباب قبل النوم."}
    ],
    # 15. الصفات
    "lesson_1771530940001_er8y5": [
        {"es": "Esta casa en la montaña es muy grande.", "ar": "هذا المنزل في الجبل كبير جداً."},
        {"es": "Tengo un perro pequeño de color blanco.", "ar": "عندي كلب صغير أبيض اللون."},
        {"es": "El carro rojo viaja muy rápido hoy aquí.", "ar": "السيارة الحمراء تسير بسرعة كبيرة اليوم هنا."},
        {"es": "El tren de carga es muy lento hoy.", "ar": "قطار البضائع بطيء جداً اليوم."},
        {"es": "El edificio del banco es muy alto hoy.", "ar": "مبنى البنك شاهق الارتفاع اليوم."},
        {"es": "La mesa de la sala es muy baja aquí.", "ar": "طاولة الصالة منخفضة جداً هنا."},
        {"es": "Compro un libro de español muy nuevo hoy.", "ar": "أشتري كتاب لغة إسبانية جديداً جداً اليوم."},
        {"es": "Tengo un pasaporte viejo en mi maleta gris.", "ar": "عندي جواز سفر قديم في حقيبتي الرمادية."},
        {"es": "La sopa de tomate está muy caliente hoy.", "ar": "حساء الطماطم ساخن جداً اليوم."},
        {"es": "Bebo agua muy fría del grifo del baño.", "ar": "أشرب ماءً بارداً جداً من صنبور الحمام."}
    ],
    # 16. الوقت
    "lesson_1771530940003_1y3gg": [
        {"es": "¿Qué hora es en este momento, por favor?", "ar": "كم الساعة في هذه اللحظة، من فضلك؟"},
        {"es": "Espera un minuto, necesito buscar mi maleta.", "ar": "انتظر دقيقة، أحتاج للبحث عن حقيبتي."},
        {"es": "El segundo pasa muy rápido en este reloj.", "ar": "الثانية تمر بسرعة كبيرة في هذه الساعة."},
        {"es": "El día está muy soleado y hermoso hoy aquí.", "ar": "اليوم مشمس وجميل للغاية هنا اليوم."},
        {"es": "Hablamos por teléfono durante la noche hoy.", "ar": "نتحدث عبر الهاتف خلال الليل اليوم."},
        {"es": "Estudio español muchas horas esta semana.", "ar": "أدرس الإسبانية ساعات عديدة هذا الأسبوع."},
        {"es": "Este mes viajo a la playa con mi familia.", "ar": "هذا الشهر أسافر إلى الشاطئ مع عائلتي."},
        {"es": "Feliz año nuevo para todos mis amigos hoy.", "ar": "عام جديد سعيد لجميع أصدقائي اليوم."},
        {"es": "Nos vemos mañana por la mañana en la oficina.", "ar": "نلتقي غداً صباحاً في المكتب."},
        {"es": "Paso la tarde libre leyendo en el jardín.", "ar": "أقضي فترة بعد الظهر الحرة في القراءة بالحديقة."}
    ],
    # 17. الطقس
    "lesson_1771530940004_6ag9o": [
        {"es": "Hace mucho sol y calor en la playa hoy.", "ar": "الجو مشمس وحار جداً في الشاطئ اليوم."},
        {"es": "Hay lluvia fuerte afuera, no quiero salir.", "ar": "هناك مطر غزير في الخارج، لا أريد الخروج."},
        {"es": "Una nube blanca cubre el cielo azul hoy.", "ar": "سحابة بيضاء تغطي السماء الزرقاء اليوم."},
        {"es": "Cae nieve blanca sobre la montaña alta hoy.", "ar": "تتساقط ثلوج بيضاء على الجبل المرتفع اليوم."},
        {"es": "Hace mucho viento frío esta tarde en la calle.", "ar": "هناك رياح باردة شديدة هذا المساء في الشارع."},
        {"es": "La tormenta de anoche hizo mucho ruido aquí.", "ar": "عاصفة الليلة الماضية أحدثت ضوضاء كبيرة هنا."},
        {"es": "Hace calor en la cocina por el horno caliente.", "ar": "الجو حار في المطبخ بسبب الفرن الساخن."},
        {"es": "Hace frío en la sala, cierra la ventana.", "ar": "الجو بارد في الصالة، أغلق النافذة."},
        {"es": "Hay niebla en la carretera esta mañana hoy.", "ar": "هناك ضباب على الطريق السريع هذا الصباح اليوم."},
        {"es": "El agua del río se convirtió en hielo sólido.", "ar": "تحولت مياه النهر إلى جليد صلب."}
    ],
    # 18. الأيام والشهور
    "lesson_1771530940004_0rzn5": [
        {"es": "Hoy es lunes y tengo que ir a la escuela.", "ar": "اليوم هو الاثنين ويجب علي الذهاب إلى المدرسة."},
        {"es": "El martes por la tarde visito a mi abuelo.", "ar": "يوم الثلاثاء بعد الظهر أزور جدي."},
        {"es": "El miércoles estudio español con el maestro.", "ar": "يوم الأربعاء أدرس الإسبانية مع المعلم."},
        {"es": "El jueves por la noche cenamos pollo asado.", "ar": "يوم الخميس ليلاً نتناول الدجاج المشوي كعشاء."},
        {"es": "El viernes termino mi trabajo muy temprano.", "ar": "يوم الجمعة أنهي عملي مبكراً جداً."},
        {"es": "El sábado libre salgo con mis mejores amigos.", "ar": "يوم السبت الحر أخرج مع أفضل أصدقائي."},
        {"es": "El domingo hace sol y voy a la playa.", "ar": "يوم الأحد مشمس وأذهب إلى الشاطئ."},
        {"es": "En enero hace mucho frío y cae nieve blanca.", "ar": "في يناير الجو بارد جداً وتتساقط الثلوج البيضاء."},
        {"es": "En junio hace calor y disfrutamos del verano.", "ar": "في يونيو الجو حار ونستمتع بالصيف."},
        {"es": "En diciembre celebramos las fiestas familiares.", "ar": "في ديسمبر نحتفل بالأعياد العائلية."}
    ],
    # 19. المشاعر
    "lesson_1771530940004_z87h5": [
        {"es": "Estoy muy feliz de estar con mi familia.", "ar": "أنا سعيد جداً لوجودي مع عائلتي."},
        {"es": "Estás triste porque tu perro está enfermo hoy.", "ar": "أنت حزين لأن كلبك مريض اليوم."},
        {"es": "El padre está enojado por el ruido de la moto.", "ar": "الأب غاضب بسبب ضوضاء الدراجة النارية."},
        {"es": "Mi hermano está cansado después de correr hoy.", "ar": "شقيقي متعب بعد الجري اليوم."},
        {"es": "El niño está asustado por el perro grande.", "ar": "الطفل خائف من الكلب الكبير."},
        {"es": "Estoy sorprendido por el regalo tan elegante.", "ar": "أنا مندهش من الهدية الأنيقة للغاية."},
        {"es": "El estudiante está aburrido en la clase hoy.", "ar": "الطالب ملان في الحصة اليوم."},
        {"es": "El abuelo está enfermo en el hospital grande.", "ar": "الجد مريض في المستشفى الكبير."},
        {"es": "Él está enamorado de la chica de la escuela.", "ar": "هو واقع في حب فتاة المدرسة."},
        {"es": "Estoy confundido con esta lección tan difícil.", "ar": "أنا مرتبك من هذا الدرس الصعب للغاية."}
    ],
    # 20. فعل "Ser" و "Estar"
    "lesson_1771530940050_54now": [
        {"es": "Yo soy un estudiante inteligente en esta escuela.", "ar": "أنا طالب ذكي في هذه المدرسة."},
        {"es": "Tú eres una buena persona y muy amable hoy.", "ar": "أنت شخص طيب ولطيف للغاية اليوم."},
        {"es": "El carro rojo es muy rápido y muy moderno.", "ar": "السيارة الحمراء سريعة جداً وحديثة للغاية."},
        {"es": "Nosotros somos hermanos y vivimos muy felices.", "ar": "نحن إخوة ونعيش سعداء للغاية."},
        {"es": "Ellos son doctores en el hospital del pueblo.", "ar": "هم أطباء في مستشفى القرية."},
        {"es": "Yo estoy muy cansado después de trabajar hoy.", "ar": "أنا متعب جداً بعد العمل اليوم."},
        {"es": "Tú estás feliz con tu nuevo teléfono móvil.", "ar": "أنت سعيد بهاتفك المحمول الجديد."},
        {"es": "El café caliente está sobre la mesa de madera.", "ar": "القهوة الساخنة تقع على الطاولة الخشبية."},
        {"es": "Nosotros estamos en el restaurante español hoy.", "ar": "نحن في المطعم الإسباني اليوم."},
        {"es": "Ellas están enfermas y necesitan ir al médico.", "ar": "هن مريضات ويحتجن للذهاب إلى الطبيب."}
    ]
}

# 1. Update app/src/main/assets/lessons.json
assets_path = "app/src/main/assets/lessons.json"
with open(assets_path, "r", encoding="utf-8") as f:
    lessons_db = json.load(f)

primary_categories = lessons_db.get("Primary Level 👶", [])
for cat in primary_categories:
    cat_id = cat.get("id")
    if cat_id in new_sentences_data:
        existing_sentences = cat.get("sentences", [])
        new_items = new_sentences_data[cat_id]
        
        # Avoid duplicate additions
        existing_texts = {s.get("es", "").lower().strip() for s in existing_sentences}
        to_add = []
        for item in new_items:
            clean_es = item["es"].lower().strip()
            if clean_es not in existing_texts:
                to_add.append(item)
        
        if to_add:
            existing_sentences.extend(to_add)
            print(f"Expanded category '{cat.get('title', {}).get('ar-SA', cat_id)}' with {len(to_add)} new sentences.")

with open(assets_path, "w", encoding="utf-8") as f:
    json.dump(lessons_db, f, ensure_ascii=False, indent=2)
print("Successfully expanded assets/lessons.json")

# 2. Update library.json
lib_path = "library.json"
with open(lib_path, "r", encoding="utf-8") as f:
    lib_data = json.load(f)

for lesson in lib_data:
    cat_id = lesson.get("id")
    if cat_id in new_sentences_data:
        existing_sentences = lesson.get("sentences", [])
        existing_texts = {s.get("es", "").lower().strip() for s in existing_sentences}
        new_items = new_sentences_data[cat_id]
        
        to_add = []
        for item in new_items:
            clean_es = item["es"].lower().strip()
            if clean_es not in existing_texts:
                to_add.append({
                    "es": item["es"],
                    "ar": item["ar"],
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
        if to_add:
            existing_sentences.extend(to_add)
            print(f"Expanded library.json lesson '{cat_id}' with {len(to_add)} sentences.")

with open(lib_path, "w", encoding="utf-8") as f:
    json.dump(lib_data, f, ensure_ascii=False, indent=2)
print("Successfully expanded library.json")

# 3. Update web-factory/src/lessons.json
web_path = "web-factory/src/lessons.json"
with open(web_path, "r", encoding="utf-8") as f:
    web_data = json.load(f)

for lesson in web_data:
    cat_id = lesson.get("id")
    if cat_id in new_sentences_data:
        existing_sentences = lesson.get("sentences", [])
        existing_texts = {s.get("es", "").lower().strip() for s in existing_sentences}
        new_items = new_sentences_data[cat_id]
        
        to_add = []
        for item in new_items:
            clean_es = item["es"].lower().strip()
            if clean_es not in existing_texts:
                to_add.append({
                    "es": item["es"],
                    "ar": item["ar"],
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
        if to_add:
            existing_sentences.extend(to_add)
            print(f"Expanded web-factory lessons.json '{cat_id}' with {len(to_add)} sentences.")

with open(web_path, "w", encoding="utf-8") as f:
    json.dump(web_data, f, ensure_ascii=False, indent=2)
print("Successfully expanded web-factory lessons.json")
