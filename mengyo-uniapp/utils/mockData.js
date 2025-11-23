// Mock Data for UI Presentation

export const banners = [
  {
    id: 1,
    image: 'https://images.unsplash.com/photo-1450779333140-c69b981485e6?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80',
    title: '给流浪的它，一个温暖的家',
    subtitle: '每一份领养都是生命的重生'
  },
  {
    id: 2,
    image: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?ixlib=rb-4.0.3&auto=format&fit=crop&w=1350&q=80',
    title: '云助养计划',
    subtitle: '即使无法领养，也能传递爱心'
  }
];

export const stats = {
  rescued: 1248,
  adopted: 856,
  sponsored: 156800
};

export const categories = [
  { id: 1, name: '汪星人', icon: '🐶' },
  { id: 2, name: '喵星人', icon: '🐱' },
  { id: 3, name: '异宠', icon: '🐰' },
  { id: 4, name: '全部', icon: '🏠' }
];

export const pets = [
  {
    id: 101,
    name: '豆豆',
    age: '2岁',
    gender: '公',
    breed: '中华田园犬',
    distance: '2.5km',
    tags: ['已绝育', '疫苗全', '亲人'],
    status: 'adoptable', // adoptable, sponsored, adopted
    image: 'https://images.unsplash.com/photo-1543466835-00a7907e9de1?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    description: '豆豆是我们在公园发现的流浪小狗，性格非常温顺，喜欢和人玩耍。已经完成了所有疫苗接种。',
    story: '被发现在公园的长椅下瑟瑟发抖，经过救助站3个月的调理，现在已经是个健康快乐的小家伙了。',
    health: '健康状况良好，已绝育，体内外驱虫已完成。',
    sponsorship: {
      target: 500,
      current: 350,
      items: [
        { name: '优质狗粮', cost: 200 },
        { name: '定期驱虫', cost: 50 },
        { name: '医疗基金', cost: 250 }
      ]
    }
  },
  {
    id: 102,
    name: '咪咪',
    age: '1岁',
    gender: '母',
    breed: '英短混血',
    distance: '5.1km',
    tags: ['疫苗全', '性格高冷'],
    status: 'sponsored',
    image: 'https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    description: '咪咪是个有点害羞的小姑娘，需要耐心的家长慢慢建立信任。',
    story: '在老旧小区的地下室被发现，当时营养不良。',
    health: '有些微皮肤病已治愈。',
    sponsorship: {
      target: 300,
      current: 300,
      items: [
        { name: '猫粮', cost: 150 },
        { name: '猫砂', cost: 50 },
        { name: '零食', cost: 100 }
      ]
    }
  },
  {
    id: 103,
    name: '布丁',
    age: '5个月',
    gender: '公',
    breed: '金毛寻回犬',
    distance: '12km',
    tags: ['活泼', '粘人'],
    status: 'adoptable',
    image: 'https://images.unsplash.com/photo-1552053831-71594a27632d?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    description: '超级活泼的大金毛幼犬，精力旺盛，适合有院子的家庭。',
    story: '因为原主人搬家被遗弃。',
    health: '完全健康。',
    sponsorship: {
      target: 800,
      current: 120,
      items: [
        { name: '大型犬粮', cost: 400 },
        { name: '钙片', cost: 100 },
        { name: '玩具', cost: 300 }
      ]
    }
  },
  {
    id: 104,
    name: '小黑',
    age: '3岁',
    gender: '公',
    breed: '黑猫',
    distance: '800m',
    tags: ['聪明', '捕鼠能手'],
    status: 'adopted',
    image: 'https://images.unsplash.com/photo-1548247416-ec66f4900b2e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    description: '机灵的小黑猫，已经找到了温暖的家。',
    story: '流浪时是小区的明星猫。',
    health: '健康。',
    sponsorship: {
      target: 200,
      current: 200,
      items: []
    }
  },
  {
    id: 105,
    name: '阿黄',
    age: '4岁',
    gender: '母',
    breed: '田园犬',
    distance: '1.2km',
    tags: ['忠诚', '护家'],
    status: 'adoptable',
    image: 'https://images.unsplash.com/photo-1583511655857-d19b40a7a54e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',
    description: '阿黄是最忠诚的朋友，虽然长相普通，但心地善良。',
    story: '在建筑工地被救助。',
    health: '腿部受过轻伤，已痊愈。',
    sponsorship: {
      target: 400,
      current: 0,
      items: [
        { name: '狗粮', cost: 200 },
        { name: '关节保养', cost: 200 }
      ]
    }
  }
];

export const user = {
  name: '爱心天使',
  avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?ixlib=rb-4.0.3&auto=format&fit=crop&w=200&q=80',
  stats: {
    adopted: 1,
    sponsored: 3,
    donated: 1250
  }
};
