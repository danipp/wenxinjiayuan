export const useRichText = () => {
    const getRichText = (richText) => {
        // 正则表达式：匹配<img>标签，捕获style属性内容（若存在）
        const imgRegex = /(<img[^>]*?)(style=["']?)([^"']*?)(["']?)([^>]*?>)/gi;

        // 替换处理函数
        const processedRichText = richText.replace(imgRegex, (match, prefix, styleAttr, styleContent, styleEnd, suffix) => {
            // 情况1：没有style属性
            if (!styleAttr) {
                return `${prefix} style="width:100%;"${suffix}`;
            }

            // 情况2：有style属性，但不含width样式
            if (!/width\s*:/i.test(styleContent)) {
                // 保留原有style内容，追加width:100%（处理末尾是否有分号）
                const newStyle = styleContent.trim()
                    ? `${styleContent.trim()}${styleContent.trim().endsWith(';') ? '' : ';'}width:100%;`
                    : 'width:100%;';
                return `${prefix}${styleAttr}${newStyle}${styleEnd}${suffix}`;
            }

            // 情况3：已有width样式，不处理
            return match;
        });
        return processedRichText;
    }
    const getVideoText = (videoText) => {
        // 正则表达式：匹配<video>标签，捕获width属性是否为auto
        const videoRegex = /(<video[^>]*?)width="auto"([^>]*?>)/gi;

        // 替换为 width="100%"
        const processedVideoText = videoText.replace(
            videoRegex,
            // 保留原标签其他属性，仅替换 width
            '$1width="100%"$2'
        );
        return processedVideoText;
    }
    return {
        getRichText,
        getVideoText
    }
}