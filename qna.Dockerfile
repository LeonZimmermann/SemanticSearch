FROM semitechnologies/qna-transformers:custom
RUN MODEL_NAME=deutsche-telekom/electra-base-de-squad2 ./download.py

