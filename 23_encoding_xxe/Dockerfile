FROM php:7.1.4-apache

EXPOSE 8023

RUN set -x \
    && mkdir /secret \
    && printf 'Got it !\nflag-1abcceff95c13c8cd7358a1e1728e0ef' >> /secret/flag_readme.txt \
    && chmod 777 /secret/flag_readme.txt

