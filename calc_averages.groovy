def classes = [:]
def processLine = { it, o ->
  if(it[0] == 'iri') { return; }
  if(it[1] == 'NO_DEFINITION') { return; }
  it[0] = it[0].replace('.txt','')
  if(!classes.containsKey(it[0])) {
    classes[it[0]] = [
      iri: it[0],
      count: 0,
      ontology: o,
      sentiments: [
        VeryPositive: 0,
        Positive: 0,
        Neutral: 0,
        Negative: 0, 
        VeryNegative: 0,
      ]
    ]
  }

  classes[it[0]].sentiments.VeryPositive += Double.parseDouble(it[3].tokenize(':')[1])
  classes[it[0]].sentiments.Positive += Double.parseDouble(it[4].tokenize(':')[1])
  classes[it[0]].sentiments.Neutral += Double.parseDouble(it[5].tokenize(':')[1])
  classes[it[0]].sentiments.Negative += Double.parseDouble(it[6].tokenize(':')[1])
  classes[it[0]].sentiments.VeryNegative += Double.parseDouble(it[7].tokenize(':')[1])
  classes[it[0]].count++
}

new File('./mesh_sentiments.txt').splitEachLine('\t') {
  processLine(it, 'MESH')
}
new File('./doid_sentiments.tsv').splitEachLine('\t') {
  processLine(it, 'DOID')
}

def head = ['VeryPositive', 'Positive', 'Neutral', 'Negative', 'VeryNegative']
println "iri\tontology\t" + head.join('\t')
classes.each { k, v ->
  def out = "$k\t${v.ontology}" 
  head.each {
    out += "\t${v.sentiments[it] / v.count}"
  }
  println out
}
