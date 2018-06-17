import re
import requests

def get_page(url):
    r = requests.get(url)
    content = r.text.encode('utf-8', 'ignore')
    return content


if __name__ == "__main__":
    url = 'https://varunmaitreya.github.io/squirrelontology.github.io/index.html'
    content = get_page(url)
    content = content.replace("\n", '')

    content_pattern = re.compile(r'<div class="container">(.*?)</div>')
    result = re.findall(content_pattern, content)
    data = result[0]

    url_pattern = re.compile(r'<a href="(vocab/.*?)">')
    problem_list = re.findall(url_pattern, data)
    print problem_list
