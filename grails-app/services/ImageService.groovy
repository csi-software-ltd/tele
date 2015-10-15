import java.awt.Image 
import java.awt.image.BufferedImage
import javax.imageio.ImageIO as IIO
import javax.imageio.metadata.IIOMetadata
import javax.imageio.stream.*
import javax.imageio.ImageReader

class ImageService {
 
  boolean transactional = false
  static final THUMBPREFIX='t_'
  static scope = "request"

  def transient m_oController=null  
  def transient m_sPathRes
  def transient m_sSessionName
  def transient m_sSessionKeepName  
  def transient m_bFolder
  
  /////////////////////////////////////////////////////////////////////////////////////////  
  private checkInit(){
    if(m_oController==null)
      log.debug("Does not set controller object in ImageService. Call imageService.init(this,....")
    return (m_oController==null)
  }
    
  /////////////////////////////////////////////////////////////////////////////////////////
  def init(oController,sSessionName, sSessionKeepName,sPathRes, bFolder = false){ //!
    m_oController=oController    
    m_sPathRes=sPathRes
    m_sSessionName=sSessionName
    m_sSessionKeepName=sSessionKeepName   
	  m_bFolder=bFolder
        
    //if(m_sPathRes[-1]!=File.separatorChar)
      //m_sPathRes+=File.separatorChar
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  private getSizes(iSize, iImageWidth,iImageHeight,iMaxHeight = 0, forThumb = false){  
    int iWidth = iSize;  
    int iHeight = iSize;  
    int iTempWidth;
    int iTempHeight;
    
    if (iMaxHeight){
      def fM
      if (forThumb) fM = Math.max(iSize/iImageWidth, iMaxHeight/iImageHeight)
      else fM = Math.min(iSize/iImageWidth, iMaxHeight/iImageHeight)
      if (fM > 1) return [height:iImageHeight,width:iImageWidth]
      else{
        iTempHeight = (int)(fM*iImageHeight)
        iTempWidth = (int)(fM*iImageWidth)
        return [height:iTempHeight,width:iTempWidth]
      }
    }
    if (iImageWidth > iImageHeight) {  
      iTempWidth = iWidth;  
      iTempHeight = (int)(((double)iImageHeight*iWidth) / iImageWidth);  
    }else {  
      iTempHeight = iHeight ;  
      iTempWidth = (int)(((double)iImageWidth*iHeight ) / iImageHeight);  
    }
    return [height:iTempHeight,width:iTempWidth]
  }  
  /////////////////////////////////////////////////////////////////////////////////////////    
  def loadPicture(sName,iWeightLimit,sDbFileName,iThumbSize,iThumbHeight = 0,bSaveThumb = true){ //!   
    def hsRes=[filename:'',error:1] // 1 - UNSPECIFIC ERROR
    if(checkInit()) 
      return hsRes
      
	  def fileImage
	  try {
      fileImage= m_oController.request.getFile(sName)
	  } catch (Exception e) {
	  }
    if(!fileImage) 
      return hsRes
      
    //FYI: fileImage.getStorageDescription() -- tmp upload dir
    def sOrignalName
    def sContentType
    
    sOrignalName=fileImage.originalFilename      
    sContentType=fileImage.getContentType()

    //log.debug("Content type for uploading file in ImageService: "+sContentType)

    def sExtention='jpg'

    //RESERVED
    if(sOrignalName==null){
      hsRes.error=2
      return hsRes 
    }
    //CHECK WEIGHT
    hsRes.maxweight = String.format('%4.1f',iWeightLimit/(1024*1024))
    if(fileImage.getSize()>iWeightLimit){
      hsRes.error=3
      return hsRes 
    }
    //CHECK CONTENT TYPE  //,"image/bmp","image/gif"  ,"image/png","image/x-png" - prohibited 
    if(!(sContentType in ["image/pjpeg","image/jpeg",
                          "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                          "application/vnd.ms-excel","application/msword"])){
      hsRes.error=4
      return hsRes
    }
    switch(sContentType){
      case "image/jpeg":
      case "image/pjpeg":
        sExtention='jpg'
        break          
      case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
      case "application/msword":
        sExtention='doc'
        break
      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
      case "application/vnd.ms-excel":
        sExtention='xls'
        break
    }
    //GENERATE NAME 
	  def sPictureName=sDbFileName.toString()+'.'+sExtention
    def sPath=m_sPathRes
    try{
      def oPicture=new Picture()
      def oPictureOld=Picture.findByFilename(sPath+sPictureName)
      if(oPictureOld)
        oPicture=oPictureOld

      oPicture.filename=sPath+sPictureName
      oPicture.filedata=fileImage.getBytes()
      oPicture.mimetype=sContentType

      if(!oPicture.save(flush:true)) {
        log.debug(" Error on save oPicture in ImageService")
        oPicture.errors.each{log.debug(it)}
      }

      //DELETE PREV VERSION OF THAT PICTURES
      if(m_oController.session[m_sSessionName]==null)
        m_oController.session[m_sSessionName]=[:]

      if(m_oController.session[m_sSessionName][sName]!=null) {
        def sOldPic=m_oController.session[m_sSessionName][sName]

        if(!(sOldPic in m_oController.session[m_sSessionKeepName])){
          deletePictureFiles(sOldPic)
        }
      }

      m_oController.session[m_sSessionName][sName]=sPictureName
      //////////////////////////////////////////////
      //  WRITE THUMBNAIL
      if (bSaveThumb&&sExtention=='jpg'){
        //RESIZE
        //BufferedImage biTemp = javax.imageio.ImageIO.read(new File(sPath+sPictureName))
        def inputStream = new ByteArrayInputStream(oPicture.filedata)
        BufferedImage biTemp = javax.imageio.ImageIO.read(inputStream)

        def iWidth=biTemp.getWidth(null)
        def iHeight=biTemp.getHeight(null)

        def hsSizes
        Image imTemp
        def imOut
        hsSizes=getSizes(iThumbSize,iWidth,iHeight,iThumbHeight, true)
        imTemp = biTemp.getScaledInstance(hsSizes.width,hsSizes.height,Image.SCALE_SMOOTH);
        def x = 0
        def y = 0

        imOut = new BufferedImage(hsSizes.width, hsSizes.height, BufferedImage.TYPE_INT_RGB);
        imOut.getGraphics().drawImage(imTemp, x, y, null);

        def outputStream = new ByteArrayOutputStream()
        try {
          //javax.imageio.ImageIO.write(imOut, sExtention, new File(sPath+THUMBPREFIX+sPictureName));
          javax.imageio.ImageIO.write(imOut, sExtention, outputStream);
        }catch (IOException e) {
          log.debug("Cannot write picture "+sPath+THUMBPREFIX+sPictureName+"\n"+e.toString())      
        }

        def oPictureThumb=new Picture()
        def oPictureThumbOld=Picture.findByFilename(sPath+THUMBPREFIX+sPictureName)
        if(oPictureThumbOld)
          oPictureThumb=oPictureThumbOld
        oPictureThumb.filename=sPath+THUMBPREFIX+sPictureName
        oPictureThumb.filedata=outputStream.toByteArray()

        if(!oPictureThumb.save(flush:true)) {
          log.debug(" Error on save oPictureThumb in ImageService")
          oPictureThumb.errors.each{log.debug(it)}
        }
      }
      //////////////////////////////////////////////	       	  	  	      	      
      
      //PUT TEMPORARY FILES INTO QUEUE FOR DELETETION UNTIL USER DO NOT SAVE IT INTO DB      
      deletePictureFiles(sPictureName)
      
      hsRes.filename=(m_bFolder?sPictureName[0..1]+'/':'')+sPictureName 
      hsRes.thumbname=(m_bFolder?sPictureName[0..1]+'/':'')+THUMBPREFIX+sPictureName      
      hsRes.error=0    
    }catch (javax.imageio.IIOException ie) {
      log.debug("Cannot read picture\n"+ie.toString())	  
      def oPictureTMP=Picture.findByFilename(sPath+sPictureName)
      if(oPictureTMP)
        oPictureTMP.delete(flush:true)      
      /*def destFile=new File(sPath+sPictureName)
      if(destFile.exists())
        destFile.delete()  
      */  
	    hsRes.error=5
      m_oController.session[m_sSessionName][sName]=null
    }catch (Exception e) {
      log.debug("Cannot save picture\n"+e.toString())
      /*def destFile=new File(sPath+sPictureName)
      if(destFile.exists())
        destFile.delete()
      */  
      def oPictureTMP=Picture.findByFilename(sPath+sPictureName)
      if(oPictureTMP)
        oPictureTMP.delete(flush:true)
      m_oController.session[m_sSessionName][sName]=null
    }
  
    return hsRes
  }   
  /////////////////////////////////////////////////////////////////////////////////////////
  def deletePicture(sName){ //!
    if(checkInit()) 
      return null
        
    if(m_oController.session[m_sSessionName]==null)
      m_oController.session[m_sSessionName]=[:]
    if(m_oController.session[m_sSessionName][sName]!=null) {
      def sOldPic=m_oController.session[m_sSessionName][sName]
      if(!(sOldPic in m_oController.session[m_sSessionKeepName]))
        deletePictureFiles(sOldPic)
    }
    m_oController.session[m_sSessionName][sName]=null
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////
  private deletePictureFiles(sMainName){
    def sPath=m_sPathRes+(m_bFolder?sMainName[0..1]+File.separatorChar:'')
    try{
      //DELETE via service
      def oTempPic=new Picturetemp([filename:sMainName,fullname:sPath+sMainName])
      oTempPic.save(flush:true)
      oTempPic=new Picturetemp([filename:THUMBPREFIX+sMainName,fullname:sPath+THUMBPREFIX+sMainName])
      oTempPic.save(flush:true)      
    }catch(Exception e){
      log.debug("Cannot put into Picturetemp table\n"+e.toString())
    }
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  private clearPictureTmpDb(lsFiles){
    def oPicturetemp=new Picturetemp() 
    oPicturetemp.csiDeleteByFilenames(lsFiles)
  }
  /////////////////////////////////////////////////////////////////////////////////////////  
  private clearPictureSession(){
    m_oController.session[m_sSessionName]=[:]
    m_oController.session[m_sSessionKeepName]=[]
  }
  /////////////////////////////////////////////////////////////////////////////////////////  
  def getSessionPics(sName){ //!
    if(checkInit()) 
      return null
        
    if((m_oController.session[m_sSessionName]==null)||(m_oController.session[m_sSessionName][sName]==null)) 
      return null
    def sPic=m_oController.session[m_sSessionName][sName]
    def sPath=(m_bFolder?sPic[0..1]+'/':'')
    def hsRes=[:]
    hsRes['photo']=sPath+sPic
    //hsRes['thumb']=sPath+THUMBPREFIX+sPic
    return hsRes 
  }
  /////////////////////////////////////////////////////////////////////////////////////////  
  private getSessionFileList(lsNames){
    def lsFiles=[]
    def sPic
    for(sName in lsNames) {
      if((m_oController.session[m_sSessionName]==null)||(m_oController.session[m_sSessionName][sName]==null)) 
        continue
      sPic=m_oController.session[m_sSessionName][sName]
      lsFiles<<sPic
      lsFiles<<THUMBPREFIX+sPic
    }
    return lsFiles 
  }
  /////////////////////////////////////////////////////////////////////////////////////////  
  private deleteOldPictureFiles(lsKeepFiles){
    def lsFiles=[]
    if(m_oController.session[m_sSessionKeepName]!=null){
      for(sFile in m_oController.session[m_sSessionKeepName])
        if(!(sFile in lsKeepFiles))
          lsFiles<<sFile
      deleteListPictureFiles(lsFiles)      
    }
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  private deleteListPictureFiles(lsFiles){
    for(sMainName in lsFiles)
      deletePictureFiles(sMainName)
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  def startFileSession(){ //!
    if(checkInit()) 
      return null
      
    if(m_oController.session[m_sSessionName]==null)
      m_oController.session[m_sSessionName]=[:]
    m_oController.session[m_sSessionKeepName]=[]
  }  
  /////////////////////////////////////////////////////////////////////////////////////////  
  def finalizeFileSession(lsNames){ //!
    if(checkInit()) 
      return null

    def lsFiles=getSessionFileList(lsNames)
    //УДАЛЯЕМ СТАРЫЕ ФАЙЛЫ  ИСКЛЮЧАЯ ТЕ, КОТОРЫЕ ОСТАВЛЕНЫ В СЕССИИ
    deleteOldPictureFiles(lsFiles)
    //УДАЛЯЕМ СОХРАНЕННЫЕ В БД ФАЙЛЫ ИЗ СПИСКА НА УДАЛЕНИЕ
    clearPictureTmpDb(lsFiles)
    //ОЧИЩАЕМ СЕССИЮ    
    clearPictureSession()
  }
  /////////////////////////////////////////////////////////////////////////////////////////
  def deletePictureFilesFromHd(lsFiles){
    lsFiles.each{deletePictureFiles(it)}
  }
/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////
}