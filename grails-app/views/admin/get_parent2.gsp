<label for="parent2">Рекомендатель 2:</label>
<g:select name="parent2" value="${client?.parent2?:0}" from="${dealers}" optionKey="id" optionValue="name" noSelection="[0:'не выбран']"/>
