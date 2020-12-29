package io.carrotquest.sample.data

import io.carrotquest.sample.model.ProductEntity

fun getDemoData() : ArrayList<ProductEntity> {
    val data = ArrayList<ProductEntity>()
    val p1 = ProductEntity(
        name = "Nike Air Force 1 '07 LV8 1",
        description = "Кроссовки Nike Air Force 1 '07 LV8 1 получили все характеристики вдохновленной баскетболом оригинальной модели 1982 года и свежие детали дизайна для изящного стиля.",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/962/450_450_1/96206eb9263020b0ddf61a1aaa50682a.jpg",
        price = 8990.0f
    )
    data.add(p1)

    val p2 = ProductEntity(
        name = "Nike Air Force 1 High 07 AN20",
        description = "Кроссовки Nike Air Force 1 High '07 — новая версия культовых AF-1, сочетающая классический баскетбольный стиль с непревзойденной амортизацией.",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/c9c/450_450_1/c9c1976d20a18c8e3ddded0c780d74b4.jpg",
        price = 9790.0f
    )
    data.add(p2)

    val p3 = ProductEntity(
        name = "Nike Kyrie Flytrap II",
        description = "Кайри Ирвинг обходит защитников благодаря своим головокружительным ложным выпадам, разворотам и молниеносным кроссоверам. Кроссовки Kyrie Flytrap II идеально дополняют его игру, обеспечивая максимальный контроль, возврат энергии и превосходное сцепление под любым углом.",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/1bd/450_450_1/1bd8fec73fac453882cbb0f03395a511.jpg",
        price = 8190.0f
    )
    data.add(p3)

    val p4 = ProductEntity(
        name = "Puma x Tyakasha RS-X",
        description = "Благодаря уникальному эклектичному дизайну бренд TYAKASHA, основанный в Шанхае, становится все более популярным в мире уличной моды. Покорение галактики астронавтами – вот тема нынешней коллекции. Инновационные кроссовки RS-X TYAKASHA представлены в двух вариантах: с накладками из замши и кожи и декорированные эффектным изображением космического монстра.",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/244/450_450_1/2446998e9f291369a142ca339293d2bf.jpg",
        price = 9990.0f
    )
    data.add(p4)

    val p5 = ProductEntity(
        name = "New Balance 997",
        description = "Модель 997 - новая классика, сочетающая лаконичный стиль и комфорт, особенно актуальные в повседневной жизни. Верх модели, выполненный из легких дышащих материалов, обеспечивает удобную посадку, в то время как облегченная промежуточная подошва EVA, поглощая ударную волну, сделает каждый шаг мягким.",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/894/450_450_1/894321543c59a39af4ad42605119496e.jpg",
        price = 12990.0f
    )
    data.add(p5)

    val p6 = ProductEntity(
        name = "Jordan Spizike",
        description = "Мужские кроссовки Jordan Spizike созданы на основе четырех легендарных моделей Air Jordan (модели III, IV, V и VI). Их гибридная конструкция посвящена Спайку Ли и легендарной голливудской рекламной кампании в поддержку баскетбола.",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/e68/450_450_1/e68b112a4da5f969eb3bec3afb9fad31.jpg",
        price = 12990.0f
    )
    data.add(p6)

    val p7 = ProductEntity(
        name = "Jordan Access",
        description = "",
        imageUri = "https://static.street-beat.ru/upload/resize_cache/iblock/fcb/450_450_1/fcbdf03d79ef3df10a8ba0cf6127760e.jpg",
        price = 8190.0f
    )
    data.add(p7)

    return data
}