try:
    import cPickle as pickle
except:
    import pickle

class LoadSaveInterface(object):
    def __init__(self):
        pass

    def saveFile(self, filepath, obj):
        #provide full path to the file as filepath and obj to save
        try:
            file = open(filepath, 'wb')
            pickle.dump(obj, file, protocol=pickle.HIGHEST_PROTOCOL)
            file.close()
        except BaseException as e:
            print("Could not save the file %s because %s" % (filepath, str(e)))

    def saveFileRaw(self, filepath, string):
        try:
            file = codecs.open(filepath, mode='w', encoding="utf-8")
            string = unicode(string, errors='replace')
            file.write(string)
            file.close()
        except BaseException as e:
            print("Could not save the file %s because %s" % (filepath, str(e)))

    def loadFile(self, filepath):
        #provide full path to the file as filepath
        file = open(filepath, 'rb')
        obj = pickle.load(file)
        file.close()
        return obj
