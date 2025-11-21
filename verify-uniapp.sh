#!/bin/bash

# UniApp 前端验证脚本

echo "========================================="
echo "  UniApp 前端验证"
echo "========================================="
echo ""

# 检查编译输出
echo "✓ 检查编译输出目录..."
if [ -d "/workspace/public/uniapp" ]; then
    echo "  ✅ 编译输出目录存在"
    echo "  📁 /workspace/public/uniapp"
else
    echo "  ❌ 编译输出目录不存在"
    exit 1
fi

# 检查 index.html
echo ""
echo "✓ 检查入口文件..."
if [ -f "/workspace/public/uniapp/index.html" ]; then
    echo "  ✅ index.html 存在"
else
    echo "  ❌ index.html 不存在"
    exit 1
fi

# 检查 assets 目录
echo ""
echo "✓ 检查资源文件..."
if [ -d "/workspace/public/uniapp/assets" ]; then
    asset_count=$(ls -1 /workspace/public/uniapp/assets | wc -l)
    echo "  ✅ assets 目录存在"
    echo "  📦 包含 $asset_count 个资源文件"
else
    echo "  ❌ assets 目录不存在"
    exit 1
fi

# 检查关键 JS 文件
echo ""
echo "✓ 检查页面 JS 文件..."
pages=("index" "rescue" "animal" "community" "user" "login")
for page in "${pages[@]}"; do
    if ls /workspace/public/uniapp/assets/*${page}*.js 1> /dev/null 2>&1; then
        echo "  ✅ ${page} 页面 JS 存在"
    else
        echo "  ⚠️  ${page} 页面 JS 未找到"
    fi
done

# 检查 API 文件
echo ""
echo "✓ 检查 API 配置..."
if grep -q "BASE_URL = '/api'" /workspace/mengyo-uniapp/utils/request.js; then
    echo "  ✅ request.js BASE_URL 已更新为 '/api'"
else
    echo "  ⚠️  request.js BASE_URL 可能未正确配置"
fi

# 检查 API 定义
echo ""
echo "✓ 检查 API 定义..."
api_modules=("rescueApi" "communityApi" "adoptionApi" "donationApi")
for module in "${api_modules[@]}"; do
    if grep -q "export const ${module}" /workspace/mengyo-uniapp/utils/api.js; then
        echo "  ✅ ${module} 已定义"
    else
        echo "  ⚠️  ${module} 未找到"
    fi
done

# 统计文件大小
echo ""
echo "✓ 统计编译产物大小..."
total_size=$(du -sh /workspace/public/uniapp | cut -f1)
echo "  📊 总大小: $total_size"

# 检查页面文件
echo ""
echo "✓ 检查源码页面..."
source_pages=("index" "rescue" "animal" "community" "user" "login")
for page in "${source_pages[@]}"; do
    page_path="/workspace/mengyo-uniapp/pages/${page}"
    if [ -d "$page_path" ]; then
        echo "  ✅ ${page} 页面目录存在"
    fi
done

echo ""
echo "========================================="
echo "  ✅ 验证完成！"
echo "========================================="
echo ""
echo "部署路径: /workspace/public/uniapp/"
echo "访问方式: http://your-domain/uniapp/"
echo ""
echo "后续步骤:"
echo "1. 配置服务端反向代理 /api/* 到后端服务"
echo "2. 启动后端服务"
echo "3. 访问前端页面测试功能"
echo ""
