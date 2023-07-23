function append () {
  const ele = document.getElementsByClassName('versteckt')[0].innerText
  console.log('do' + ele)
  const neu = ' :📧'
  const m = document.createElement('span')
  m.innerText = neu
  document.getElementsByClassName('versteckt')[0].appendChild(m)
}
