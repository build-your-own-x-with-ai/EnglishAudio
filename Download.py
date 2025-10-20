#!/usr/bin/env python3

import pathlib
import re
import requests
from bs4 import BeautifulSoup

HEADERS = {
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
    "Referer": "https://mp.weixin.qq.com/",
    "Cookie": ""  # 403时补充微信登录Cookie
}

def download_audio(audio_url, save_path):
    """仅下载音频，跳过已存在的文件"""
    # 关键：如果文件已存在，直接跳过，不重复下载
    if save_path.exists():
        print(f"⏭️  文件已存在，跳过：{save_path.name}")
        return True
    try:
        response = requests.get(audio_url, headers=HEADERS, stream=True, timeout=15)
        response.raise_for_status()
        with open(save_path, "wb") as f:
            for chunk in response.iter_content(chunk_size=1024):
                if chunk:
                    f.write(chunk)
        print(f"✅ 成功下载：{save_path.name}")
        return True
    except Exception as e:
        print(f"❌ 下载失败 {save_path.name}：{str(e)[:50]}")
        return False

def extract_secondary_audio(secondary_url, grade_save_dir):
    try:
        response = requests.get(secondary_url, headers=HEADERS, timeout=10)
        response.raise_for_status()
    except Exception as e:
        print(f"⚠️ 访问二级页面失败 {secondary_url}：{str(e)}")
        return

    soup = BeautifulSoup(response.text, "html.parser")
    page_title = soup.find("h1", class_="rich_media_title") or soup.find("h1")
    page_title = page_title.text.strip() if page_title else "未命名音频"
    safe_title = re.sub(r'[\\/*?:"<>|]', "_", page_title)

    audio_fileid = re.findall(r'voice_encode_fileid="(.*?)"', response.text)
    if audio_fileid:
        audio_url = f"https://res.wx.qq.com/voice/getvoice?mediaid={audio_fileid[0]}"
        audio_save_path = grade_save_dir / f"{safe_title}.mp3"
        download_audio(audio_url, audio_save_path)
    else:
        print(f"⚠️ 二级页面 {safe_title} 无音频资源")

def crawl_grade_audio(main_url, root_save_dir):
    root_path = pathlib.Path(root_save_dir)
    root_path.mkdir(parents=True, exist_ok=True)
    print(f"📂 根保存目录已创建：{root_path}")

    try:
        main_response = requests.get(main_url, headers=HEADERS, timeout=10)
        main_response.raise_for_status()
        main_soup = BeautifulSoup(main_response.text, "html.parser")
    except Exception as e:
        print(f"❌ 访问一级页面失败：{str(e)}")
        return

    # -------------------------- 核心修复：去重年级分区 --------------------------
    grade_sections = []
    processed_grades = set()  # 用集合记录已处理的年级，避免重复
    down_arrow_titles = main_soup.find_all(lambda tag: 
        tag.name in ["p", "div", "h3"] and "↓↓" in tag.text
    )

    for title_tag in down_arrow_titles:
        raw_title = title_tag.text.strip()
        grade_match = re.search(r'(一|二|三|四|五|六)年级上册\s*(课本|单词)', raw_title)
        if not grade_match:
            continue
        
        pure_grade_title = grade_match.group().replace(" ", "")
        # 关键：如果该年级已处理过，直接跳过
        if pure_grade_title in processed_grades:
            continue
        processed_grades.add(pure_grade_title)  # 标记为已处理

        next_table = title_tag.find_next("table")
        if next_table:
            grade_sections.append((pure_grade_title, next_table))
    # --------------------------------------------------------------------------------

    if not grade_sections:
        print("⚠️ 未找到任何有效年级分区")
        return
    print(f"\n🔍 共找到 {len(grade_sections)} 个有效年级分区（已去重）")

    # 按年级处理（无重复）
    for grade_title, table in grade_sections:
        print(f"\n===== 开始处理：{grade_title} =====")
        grade_save_dir = root_path / grade_title
        grade_save_dir.mkdir(parents=True, exist_ok=True)

        secondary_links = []
        a_tags = table.find_all("a")
        for a_tag in a_tags:
            link = a_tag.get("href")
            if link and link.startswith("http"):
                secondary_links.append(link)

        if not secondary_links:
            print(f"⚠️ {grade_title} 表格无有效链接")
            continue
        print(f"🔗 {grade_title} 找到 {len(secondary_links)} 个二级页面链接")

        for idx, link in enumerate(secondary_links, 1):
            print(f"\n--- 处理 {grade_title} 第 {idx}/{len(secondary_links)} 个链接 ---")
            extract_secondary_audio(link, grade_save_dir)

    print(f"\n🎉 所有年级音频处理完成！总目录：{root_path}")

if __name__ == "__main__":
    MAIN_ARTICLE_URL = "https://mp.weixin.qq.com/s/1sdIH_nKmo5qT4p61waqwQ"
    ROOT_SAVE_DIR = "./"
    crawl_grade_audio(MAIN_ARTICLE_URL, ROOT_SAVE_DIR)
