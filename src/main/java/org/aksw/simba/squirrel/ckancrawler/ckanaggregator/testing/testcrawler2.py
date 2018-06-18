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

    ##this block will identify in the html page from where it has to start parsing
    content_pattern = re.compile(r'<div class="container">(.*?)</div>')
    result = re.findall(content_pattern, content)
    data = result[0]

    ##this block will match specific string
    url_pattern = re.compile(r'<a href="(vocab/.*?)">')
    problem_list = re.findall(url_pattern, data)
    url_pattern2 = re.compile(r'http://.*?')
    problem_list2 = re.findall(url_pattern2, data)
    print problem_list
    print problem_list2
