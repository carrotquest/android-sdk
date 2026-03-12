package io.carrotquest.sample.data

import io.carrotquest.sample.model.ProductEntity

fun getDemoData() : ArrayList<ProductEntity> {
    val data = ArrayList<ProductEntity>()
    val p1 = ProductEntity(
        name = "Nike Air Force 1 '07 LV8 1",
        description = "Кроссовки Nike Air Force 1 '07 LV8 1 получили все характеристики вдохновленной баскетболом оригинальной модели 1982 года и свежие детали дизайна для изящного стиля.",
        imageUri = "https://cdn.sneakerbaron.nl/uploads/2022/10/10095952/Nike-Air-Force-1-07-LV8-Wit-DQ7658-100-700x700.png",
        price = 8990.0f
    )
    data.add(p1)

    val p2 = ProductEntity(
        name = "Nike Air Force 1 High 07 AN20",
        description = "Кроссовки Nike Air Force 1 High '07 — новая версия культовых AF-1, сочетающая классический баскетбольный стиль с непревзойденной амортизацией.",
        imageUri = "https://static.street-beat.ru/upload/iblock/aa1/aa17968d3fca8422523617c3d9d9e006.jpg",
        price = 9790.0f
    )
    data.add(p2)

    val p3 = ProductEntity(
        name = "Nike Kyrie Flytrap 5",
        description = "Движения Кайри настолько резкие и быстрые, что кажется, будто остальные игроки застыли на месте. Кроссовки Kyrie Flytrap 5 обеспечивают превосходное сцепление не только под стопой, но и по бокам, что позволяет резко менять направление движения.Новая блестящая модель Flytrap с дизайном от Кайри дополнена фиксирующими ремешками и подушкой Zoom Air в передней части стопы.",
        imageUri = "https://i.ebayimg.com/images/g/UfIAAOSwxGpjgjnH/s-l1200.png",
        price = 10_899.0f
    )
    data.add(p3)

    val p4 = ProductEntity(
        name = "Converse Chuck 70 Plant Love",
        description = "Приземленные цвета и благодарность исходят от Chuck 70, окутывая этот веселый образ приятной атмосферой. Графика, вдохновленная матерью-природой, охватывает верх из хлопкового холста, а классические полосы на средней подошве, резиновый бампер на носке и резиновый носок делают этот стиль частью наследия Converse. Современные обновления, такие как стелька OrthoLite и строчка в виде крыльев на язычке, обеспечивают комфорт.\n",
        imageUri = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR8Nq7G53nccuWM8pMGdL0V7SyiRnktt4aopg&s",
        price = 16_199.0f
    )
    data.add(p4)

    val p5 = ProductEntity(
        name = "New Balance 997",
        description = "Модель 997 - новая классика, сочетающая лаконичный стиль и комфорт, особенно актуальные в повседневной жизни. Верх модели, выполненный из легких дышащих материалов, обеспечивает удобную посадку, в то время как облегченная промежуточная подошва EVA, поглощая ударную волну, сделает каждый шаг мягким.",
        imageUri = "https://static.street-beat.ru/upload/iblock/874/8743fce100d3c7baefa45188d741b30a.jpg",
        price = 18_999.0f
    )
    data.add(p5)

    val p6 = ProductEntity(
        name = "Jordan Max Aura 3",
        description = "Jordan Max Aura 3 — это твоя часть наследия и истории Jordan. Вдохновленная богатым наследием баскетбольного бренда эта модель передает мощную энергетику игровых кроссовок и предстает в свежем дизайне на основе классических деталей",
        imageUri = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOlN9DHDq7h3X3Nh5OrOoAFekb6obXceb8jA&s",
        price = 14_999.0f
    )
    data.add(p6)

    val p7 = ProductEntity(
        name = "PUMA CA Pro Re.Gen",
        description = "Классная и повседневная коллекция California, ставшая популярной в 80-х годах, уже давно пользуется успехом у поклонников кроссовок. Объединяя этот исторический силуэт Ca Pro с линейкой RE.GEN, построенной на принципах устойчивого развития (sustainability), конечный продукт становится не только гладким и стильным, но и полезным для человечества.",
        imageUri = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSfp6tsuaKx1Sqs76b1S_UgrQLXYKeXALj4fA&s",
        price = 11_499.0f
    )
    data.add(p7)

    return data
}