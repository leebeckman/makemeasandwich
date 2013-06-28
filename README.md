makemeasandwich
===============

**Automated ordering from Specialties**

**Requirements:**
* java 1.6/1.7
* ant
* A Specialtys account with a credit card on file

**Build:**
* Use ant to build

**Install:**
* In build/ you will find mmas/. Put that somewhere, and put mmas/bin on your path.

**Configure:**
* Edit mmas/mmas.config to configure account/ordering details.

* Make mmas.config only accessible to super-user:
```
sudo chown root:root mmas.config
sudo chmod 600 mmas.config
```

**Order a sandwich:**
```
sudo makemeasandwich
```

